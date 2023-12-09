package br.makesoftware.contacts_manager.logging;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import br.makesoftware.contacts_manager.R;
import br.makesoftware.contacts_manager.constants.NotificationChannel;

public class NotificationSender {
    public static final String NOTIFICATION_DEFAULT_TITLE = "Contact background saver";

    public static void createAllNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        for (NotificationChannel channel : NotificationChannel.values()) {
            createNotificationChannel(channel.name(), channel.getName(), channel.getDescription(), channel.getImportance(), context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createNotificationChannel(String channelId, String name, String description, int importance, Context context) {
        android.app.NotificationChannel channel = new android.app.NotificationChannel(channelId, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Notification sendSimpleNotification(String message, Context context) {
        Notification notification = new NotificationCompat.Builder(context, NotificationChannel.ALERT.name())
                .setContentTitle(NOTIFICATION_DEFAULT_TITLE)
                .setContentText(message)
                .setSmallIcon(R.drawable.launch_background)
                .build();

        int notificationId = (int) (Math.random() * 10);
        sendNotification(notification, notificationId, context);

        return notification;
    }

    public static int sendNotification(Notification notification, int notificationId, Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, notification);

        return notificationId;
    }
}
