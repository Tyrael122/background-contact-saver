package br.makesoftware.contacts_manager.services;

import static br.makesoftware.contacts_manager.constants.LogType.STATUS;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import br.makesoftware.contacts_manager.MainActivity;
import br.makesoftware.contacts_manager.R;
import br.makesoftware.contacts_manager.adapters.MockApiAdapter;
import br.makesoftware.contacts_manager.adapters.XmlApiAdapter;
import br.makesoftware.contacts_manager.constants.EndpointsConstants;
import br.makesoftware.contacts_manager.constants.NotificationChannel;
import br.makesoftware.contacts_manager.interfaces.ContactRepository;
import br.makesoftware.contacts_manager.contacts.ContactRepositoryImpl;
import br.makesoftware.contacts_manager.interfaces.ContactApiAdapter;
import br.makesoftware.contacts_manager.logging.ConcernedPeopleNotifier;
import br.makesoftware.contacts_manager.logging.Logger;
import br.makesoftware.contacts_manager.logging.NotificationSender;
import br.makesoftware.contacts_manager.util.DateUtil;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ForegroundContactService extends Service {
    private static long requestIntervalMinutes = 5;
    private static final int ONGOING_NOTIFICATION_ID = 1;
    private static final String WAKELOCK_TAG = "ContactsManager::WakelockTag";

    private PowerManager.WakeLock wakeLock;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private AutoContactSaver autoContactSaver;
    private final Runnable apiRequestRunnable = () -> {
        if (autoContactSaver == null) {
            autoContactSaver = createAutoContactSaver(getApplicationContext());
        }

        autoContactSaver.savePendingContacts();

        updateConcernedPeopleAboutNextExecution();
    };

    public static void setUrl(String url) {
        EndpointsConstants.RETRIEVE_CONTACTS_TO_SAVE_ENDPOINT = url;
    }

    @NonNull
    private AutoContactSaver createAutoContactSaver(Context context) {
        ConcernedPeopleNotifier concernedPeopleNotifier = new ConcernedPeopleNotifier(context);
        ContactApiAdapter contactApiAdapter = new XmlApiAdapter();
        ContactRepository contactRepository = new ContactRepositoryImpl(context.getContentResolver());

        return new AutoContactSaver(contactApiAdapter, contactRepository, concernedPeopleNotifier);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Notification onGoingNotification = createOnGoingNotification("O serviço está em execução com intervalo de " + requestIntervalMinutes + " minuto(s).");
        NotificationSender.sendNotification(onGoingNotification, ONGOING_NOTIFICATION_ID, getApplicationContext());

        startForeground(ONGOING_NOTIFICATION_ID, onGoingNotification);

        acquireWakelock();

        executor.scheduleAtFixedRate(apiRequestRunnable, 0, requestIntervalMinutes, TimeUnit.MINUTES);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Toast.makeText(this, "Iniciando serviço...", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Parando serviço...", Toast.LENGTH_SHORT).show();

        wakeLock.release();

        stopService();
    }

    private Notification createOnGoingNotification(String notificationText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, NotificationChannel.ONGOING.name()).setContentTitle(NotificationSender.NOTIFICATION_DEFAULT_TITLE).setContentText(notificationText).setSmallIcon(R.drawable.launch_background).setContentIntent(pendingIntent).setOngoing(true).build();
    }

    private void acquireWakelock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG);
        wakeLock.acquire();
    }

    private void updateConcernedPeopleAboutNextExecution() {
        String nextExecutionText = getNextExecutionText();

        updateNextExecutionNotification(nextExecutionText);
        Logger.logInfo(nextExecutionText, STATUS);
    }

    @NonNull
    private static String getNextExecutionText() {
        LocalDateTime nextExecution = LocalDateTime.now().plusMinutes(requestIntervalMinutes);
        return "Próxima execução agendada para às " + DateUtil.formateDate(nextExecution, "HH:mm:ss");
    }

    private void updateNextExecutionNotification(String nextExecutionText) {
        Notification onGoingNotification = createOnGoingNotification(nextExecutionText);
        NotificationSender.sendNotification(onGoingNotification, ONGOING_NOTIFICATION_ID, getApplicationContext());
    }

    private void stopService() {
        executor.shutdownNow();

        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);

            // TODO: Fix this
//            if (executor.isShutdown()) {
//                FileLogger.logInfo("O serviço foi parado com sucesso.", STATUS);
//            } else {
//                FileLogger.logError("Ocorreu um erro ao parar o serviço: " + e.getMessage(), STATUS);
//            }

        } catch (InterruptedException e) {
            Logger.logError("Ocorreu um erro ao parar o serviço: " + e.getMessage(), STATUS);
        }
    }

    public static void setRequestIntervalMinutes(int minutes) {
        requestIntervalMinutes = minutes;
    }
}
