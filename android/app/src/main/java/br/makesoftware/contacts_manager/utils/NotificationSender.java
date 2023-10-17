package br.makesoftware.contacts_manager.utils;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import br.makesoftware.contacts_manager.R;

public class NotificationSender {
    public static int sendNotification(String message, Context context, String channelId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Contact background saver")
                .setContentText(message).setSmallIcon(R.drawable.launch_background);

        int notificationId = (int) (Math.random() * 10);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());

        return notificationId;
    }
}
