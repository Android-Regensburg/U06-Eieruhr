package de.ur.mi.android.tasks.eggtimer.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    private Context context;
    public static int NOTIFICATION_ID_TIMER_SERVICE = 1;

    public NotificationHelper(Context context){
        this.context = context;
    }

    private static final String NOTIFICATION_CHANNEL_ID = "U06_TIMER_CHANNEL_ID";
    private static final String NOTIFICATION_CHANNEL_NAME = "Timer Channel";
    private static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Handles all notifications";

    public void createNotificationChannel()
    {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                                                              NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    public Notification createNotification(String title, String message, int iconID, @Nullable PendingIntent pendingIntent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(iconID)
                .setOnlyAlertOnce(true);
        if (pendingIntent != null){
            builder.setContentIntent(pendingIntent);
        }
        return builder.build();
    }

    public PendingIntent createContentIntent(Class target)
    {
        Intent intent = new Intent(context, target);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    public void showNotification(int id, Notification notification){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

}
