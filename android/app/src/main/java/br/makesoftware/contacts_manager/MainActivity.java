package br.makesoftware.contacts_manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import br.makesoftware.contacts_manager.constants.LogConstants;
import br.makesoftware.contacts_manager.utils.FileLogger;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    public static final String CHANNEL = "br.makesoftware.contacts_manager/channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler((call, result) -> {
            switch (call.method) {
                case "startService":
                    int repeatInterval = call.argument("requestInterval");
                    PeriodicWorkRequest contactsWorkRequest = new PeriodicWorkRequest.Builder(ContactWorker.class, repeatInterval, TimeUnit.MINUTES).build();
//                    OneTimeWorkRequest contactsWorkRequest = new OneTimeWorkRequest.Builder(ContactWorker.class)
//                            .setInitialDelay(10, TimeUnit.SECONDS)
//                            .build();

                    WorkManager.getInstance(getApplicationContext()).enqueue(contactsWorkRequest);

                    result.success(true);
                    break;
                case "stopService":
                    boolean hasStoppedSucessfully = ContactWorker.stopAllServices(getApplicationContext());
                    result.success(hasStoppedSucessfully);
                    break;
                case "logError":
                    new FileLogger(getApplicationContext().getFilesDir(), LogConstants.STATUS_LOGGER_NAME).logError(call.argument("message"));
                    break;
                case "logInfo":
                    new FileLogger(getApplicationContext().getFilesDir(), LogConstants.STATUS_LOGGER_NAME).logInfo(call.argument("message"));
                    break;
                default:
                    result.notImplemented();
                    break;
            }
        });
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Example Channel";
            String description = "This is an example notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}


