package de.ur.mi.android.tasks.eggtimer.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.Preference;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import de.mi.eggtimer.R;
import de.ur.mi.android.tasks.eggtimer.Timer;
import de.ur.mi.android.tasks.eggtimer.TimerActivity;
import de.ur.mi.android.tasks.eggtimer.broadcast.TimerBroadcastListener;
import de.ur.mi.android.tasks.eggtimer.broadcast.TimerBroadcastReceiver;
import de.ur.mi.android.tasks.eggtimer.notifications.NotificationHelper;

public class TimerService extends Service {

    private final NotificationHelper notificationHelper;
    private final Timer timer;
    private PowerManager.WakeLock wakeLock;

    public TimerService() {
        notificationHelper = new NotificationHelper(this);
        timer = new Timer(new TimerBroadcastListener() {
            @Override
            public void onTimerUpdate(int remainingTimeInSeconds) {
                broadcastTimerUpdate(remainingTimeInSeconds);
                adjustNotification(remainingTimeInSeconds);
            }

            @Override
            public void onTimerFinished() {
                broadcastTimerFinished();
                adjustNotification(getString(R.string.notif_message_finished));
            }

            @Override
            public void onTimerCancelled() {
                broadcastTimerCancelled();
                adjustNotification(getString(R.string.notif_message_cancelled));
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NotificationHelper.NOTIFICATION_ID_TIMER_SERVICE,
                notificationHelper.createNotification(getString(R.string.notif_title),
                        getString(R.string.notif_message_update).replace("$TIME", String.valueOf(intent.getIntExtra(Timer.TIME_KEY, 0))),
                        R.drawable.ic_launcher,
                        notificationHelper.createContentIntent(TimerActivity.class)));

        int time = intent.getIntExtra(Timer.TIME_KEY, 0);
        timer.setTime(time);
        timer.start();
        setTimerRunning(true);
        initWakeLock(time);
        return START_STICKY;
    }

    private void initWakeLock(int time) {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Timer::WakeLockTag");
        wakeLock.acquire(time);
    }

    @Override
    public void onDestroy() {
        timer.stop();
        setTimerRunning(false);
        wakeLock.release();
        stopForeground(false);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void adjustNotification(int remainingTimeInSeconds){
        Notification notification = notificationHelper.createNotification(getString(R.string.notif_title),
                getString(R.string.notif_message_update).replace("$TIME", String.valueOf(remainingTimeInSeconds)),
                R.drawable.ic_launcher,
                notificationHelper.createContentIntent(TimerActivity.class));
        notificationHelper.showNotification(NotificationHelper.NOTIFICATION_ID_TIMER_SERVICE, notification);
    }

    private void adjustNotification(String message){
        Notification notification = notificationHelper.createNotification(getString(R.string.notif_title),
                message,
                R.drawable.ic_launcher,
                notificationHelper.createContentIntent(TimerActivity.class));
        notificationHelper.showNotification(NotificationHelper.NOTIFICATION_ID_TIMER_SERVICE, notification);
    }

    private void broadcastTimerUpdate(int remainingTime)
    {
        Intent intent = TimerBroadcastReceiver.getUpdateIntent(remainingTime);
        sendBroadcast(intent);
    }

    private void broadcastTimerFinished()
    {
        Intent intent = TimerBroadcastReceiver.getEndIntent();
        sendBroadcast(intent);
    }

    private void broadcastTimerCancelled()
    {
        Intent intent = TimerBroadcastReceiver.getCancelledIntent();
        sendBroadcast(intent);
    }

    private void setTimerRunning(boolean isRunning){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.edit().putBoolean(Timer.TIME_KEY, isRunning).apply();
    }
}