package de.ur.mi.android.tasks.timer.timer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import de.ur.mi.android.tasks.timer.R;
import de.ur.mi.android.tasks.timer.TimerActivity;

public class TimerNotificationManager {

    private static int nextNotificationID = 1;

    private static final String CHANNEL_ID = "QuickTimerNotification";
    private static final String CHANNEL_NAME = "QuickTimer";
    private static final String CHANNEL_DESCRIPTION = "Notififcations from the QuickTimer app";

    private final Context context;
    private final NotificationManager manager;

    public TimerNotificationManager(Context context) {
        this.context = context;
        manager = context.getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_DESCRIPTION);
        manager.createNotificationChannel(channel);
    }

    public TimerNotificationWrapper getNotificationWrapper(String title, String message, Class<TimerActivity> target) {
        Notification notification = createNotification(title, message, target);
        return new TimerNotificationWrapper(notification);
    }

    private Notification createNotification(String title, String message, Class<TimerActivity> target) {
        Intent intent = new Intent(context, target);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher)
                .setOnlyAlertOnce(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE));
        return builder.build();
    }

    public class TimerNotificationWrapper {

        public final int id;
        public final Notification notification;

        TimerNotificationWrapper(Notification notification) {
            this.notification = notification;
            this.id = nextNotificationID++;
        }

        TimerNotificationWrapper(Notification notification, int id) {
            this.notification = notification;
            this.id = id;
        }

        public void show() {
            manager.notify(id, notification);
        }

        public TimerNotificationWrapper update(String title, String message, Class<TimerActivity> target) {
            Notification notification = createNotification(title, message, target);
            return new TimerNotificationWrapper(notification, this.id);
        }

    }

}
