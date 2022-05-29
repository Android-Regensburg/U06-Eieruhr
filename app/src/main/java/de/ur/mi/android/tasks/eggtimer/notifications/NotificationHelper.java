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

/**
 * Diese Klasse kümmert sich um das erstellen und anzeigen von Notifications.
 * Mehr zu dem Thema Notifications gibt es auf der <a href="https://developer.android.com/training/notify-user/build-notification">
 *     Android Developer WebPage</a>.
 */
public class NotificationHelper {

    private Context context;
    public static int NOTIFICATION_ID_TIMER_SERVICE = 1;

    public NotificationHelper(Context context){
        this.context = context;
    }

    private static final String NOTIFICATION_CHANNEL_ID = "U06_TIMER_CHANNEL_ID";
    private static final String NOTIFICATION_CHANNEL_NAME = "Timer Channel";
    private static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Handles all notifications";

    /**
     * Diese Methode erstellt einen NotificationChannel, über den anschließend Notifications
     * verschickt werden können.
     */
    public void createNotificationChannel()
    {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                                                              NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    /**
     * Diese Methode erstellt eine Notification.
     * @param title Titel der Notification.
     * @param message Inhalt der Notification.
     * @param iconID Icon der Notification.
     * @param pendingIntent Der Intent der Notification, dieser gibt an welche Activity gestartet
     *                      wird, wenn ein User die Notification antippt. Dieser PendingIntent kann
     *                      bzw. darf null sein.
     * @return Die erstellte Notification
     */
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

    /**
     * Erstellt einen PendingIntent, dieser kann Bestandteil einer Notification sein und gibt an,
     * welche Activity gestartet wird, wenn der User die Notification antippt.
     * @param target Die Klasse die als Ziel dient.
     * @return Den erstellten PendingIntent.
     */
    public PendingIntent createContentIntent(Class target)
    {
        Intent intent = new Intent(context, target);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    /**
     * Zeigt eine Notification an
     * @param id Die ID der Notification.
     * @param notification Die Notification, die angezeigt werden soll.
     */
    public void showNotification(int id, Notification notification){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

}
