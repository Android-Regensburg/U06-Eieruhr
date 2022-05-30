package de.ur.mi.android.tasks.timer.timer;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;

import de.ur.mi.android.tasks.timer.R;
import de.ur.mi.android.tasks.timer.TimerActivity;
import de.ur.mi.android.tasks.timer.broadcast.TimerBroadcastReceiver;

public class TimerService extends Service implements TimerFactory.TimerListener {

    private final TimerServiceBinder binder = new TimerServiceBinder();
    private PowerManager.WakeLock wakeLock;
    private TimerNotificationManager.TimerNotificationWrapper wrapper;
    private TimerFactory.Timer timer;

    @Override
    public void onCreate() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Timer::WakeLockTag");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void start(int secondsForTimer) {
        timer = TimerFactory.createTimer(secondsForTimer, this);
        timer.start();
    }

    public void pause() {
        if (timer != null) {
            timer.pause();
        }
    }

    public void resume() {
        if (timer != null) {
            timer.resume();
        }
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        } else {
            Intent intent = TimerBroadcastReceiver.getStopIntent();
            sendBroadcast(intent);
        }
    }

    private String secondsToFormattedTime(int seconds) {
        int hoursToShow = seconds / (60 * 60);
        int minutesToShow = seconds / 60;
        int secondsToShow = seconds % 60;
        String hoursForDisplay = hoursToShow > 10 ? "" + hoursToShow : "0" + hoursToShow;
        String minutesForDisplay = minutesToShow > 10 ? "" + minutesToShow : "0" + minutesToShow;
        String secondsForDisplay = secondsToShow > 10 ? "" + secondsToShow : "0" + secondsToShow;
        return hoursForDisplay + ":" + minutesForDisplay + ":" + secondsForDisplay;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        timer.stop();
        stopForeground(false);
        super.onDestroy();
    }

    @Override
    public void onTimerStarted() {
        TimerNotificationManager manager = new TimerNotificationManager(this);
        wrapper = manager.getNotificationWrapper(getString(R.string.notification_title), secondsToFormattedTime(timer.getRemainingTime()), TimerActivity.class);
        startForeground(wrapper.id, wrapper.notification);
        wakeLock.acquire(timer.getRemainingTime());
        Intent intent = TimerBroadcastReceiver.getStartIntent();
        sendBroadcast(intent);
    }

    @Override
    public void onTimerResumed() {
        wakeLock.acquire(timer.getRemainingTime());
        Intent intent = TimerBroadcastReceiver.getResumeIntent();
        sendBroadcast(intent);
    }

    @Override
    public void onTimerPaused() {
        wrapper = wrapper.update(getString(R.string.notification_title), getString(R.string.notification_message_timer_paused), TimerActivity.class);
        wrapper.show();
        wakeLock.release();
        Intent intent = TimerBroadcastReceiver.getPauseIntent();
        sendBroadcast(intent);
    }

    @Override
    public void onTimerStopped() {
        wrapper = wrapper.update(getString(R.string.notification_title), getString(R.string.notification_message_timer_completed), TimerActivity.class);
        wrapper.show();
        wakeLock.release();
        Intent intent = TimerBroadcastReceiver.getStopIntent();
        sendBroadcast(intent);
    }

    @Override
    public void onTimerUpdated(int remainingTimeInSeconds) {
        wrapper = wrapper.update(getString(R.string.notification_title), secondsToFormattedTime(remainingTimeInSeconds), TimerActivity.class);
        wrapper.show();
        Intent intent = TimerBroadcastReceiver.getUpdateIntent(remainingTimeInSeconds);
        sendBroadcast(intent);
    }

    public class TimerServiceBinder extends Binder {

        public TimerService getService() {
            return TimerService.this;
        }

    }

}
