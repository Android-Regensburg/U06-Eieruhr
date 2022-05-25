package de.ur.mi.android.tasks.eggtimer.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import de.mi.eggtimer.R;
import de.ur.mi.android.tasks.eggtimer.Timer;
import de.ur.mi.android.tasks.eggtimer.TimerActivity;
import de.ur.mi.android.tasks.eggtimer.broadcast.TimerBroadcastListener;
import de.ur.mi.android.tasks.eggtimer.broadcast.TimerBroadcastReceiver;
import de.ur.mi.android.tasks.eggtimer.notifications.NotificationHelper;
import de.ur.mi.android.tasks.eggtimer.prefs.PreferenceHelper;

/**
 * Dieser Service übernimmt die Aufgabe den Timer im Hintergrund laufen zu lassen.
 * Mehr Informationen zu Services gibt es auf der <a href="https://developer.android.com/guide/components/services">
 *     Android Developer WebPage</a>.
 */
public class TimerService extends Service {

    private NotificationHelper notificationHelper;
    private Timer timer;
    private PowerManager.WakeLock wakeLock;
    private PreferenceHelper preferenceHelper;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initNotifications();
        startForeground(NotificationHelper.NOTIFICATION_ID_TIMER_SERVICE,
                notificationHelper.createNotification(getString(R.string.notif_title),
                        getString(R.string.notif_message_update).replace("$TIME", String.valueOf(intent.getIntExtra(Timer.TIME_KEY, 0))),
                        R.drawable.ic_launcher,
                        notificationHelper.createContentIntent(TimerActivity.class)));
        initPreferences();
        initTimer(intent);
        setTimerRunning(true);
        initWakeLock(getTimeFromIntent(intent));
        return START_STICKY;
    }

    /**
     * Initialisiert das Objekt der Klasse {@link NotificationHelper}.
     */
    private void initNotifications() {
        notificationHelper = new NotificationHelper(this);
    }

    /**
     * Initialisiert das Objekt der Klasse {@link PreferenceHelper}.
     */
    private void initPreferences() {
        preferenceHelper = new PreferenceHelper(this);
    }


    /**
     * Initialisiert den {@link Timer}.
     * @param intent Der Intent der diesen Service startet und der über ein int-extra die Laufzeit
     *               des Timers mitliefert
     */
    private void initTimer(Intent intent) {
        int time = getTimeFromIntent(intent);
        //Wir erzeugen einen neuen TimerBroadcastListener, um im Service Broadcasts zu den richtigen
        // Zeitpunkten zu verschicken
        timer = new Timer(new TimerBroadcastListener() {
            @Override
            public void onTimerUpdate(int remainingTimeInSeconds) {
                broadcastTimerUpdate(remainingTimeInSeconds);
                adjustNotification(remainingTimeInSeconds);
            }

            @Override
            public void onTimerFinished() {
                stopSelf();
                broadcastTimerFinished();
                adjustNotification(getString(R.string.notif_message_finished));
            }

            @Override
            public void onTimerCancelled() {
                stopSelf();
                broadcastTimerCancelled();
                adjustNotification(getString(R.string.notif_message_cancelled));
            }
        });
        timer.setTime(time);
        timer.start();
    }

    private int getTimeFromIntent(Intent intent) {
        return intent.getIntExtra(Timer.TIME_KEY, 0);
    }

    /**
     * Initialisiert das WakeLock, welches dafür sorgt, dass der Service problemlos weiterläuft
     * selbst wenn das Smartphone im Standby bzw. Deep-Sleep ist.
     *
     * Mehr Informationen zum WakeLock: <a href="https://developer.android.com/training/scheduling/wakelock">
     *     Android Developer WebPage</a>
     *
     * @param time Die Zeit nach welche das WakLock automatisch vom Android System released wird.
     */
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


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Passt die Notification des Service an und ändert den Text so, dass die Anzeige den verbleibenden
     * Sekunden auf dem Timer entspricht.
     * @param remainingTimeInSeconds Die verbleibende Laufzeit des Timers.
     */
    private void adjustNotification(int remainingTimeInSeconds){
        Notification notification = notificationHelper.createNotification(getString(R.string.notif_title),
                getString(R.string.notif_message_update).replace("$TIME", String.valueOf(remainingTimeInSeconds)),
                R.drawable.ic_launcher,
                notificationHelper.createContentIntent(TimerActivity.class));
        notificationHelper.showNotification(NotificationHelper.NOTIFICATION_ID_TIMER_SERVICE, notification);
    }

    /**
     * Passt die Notification des Service an und ändert den Content der Notification so, dass bei
     * abgelaufenem oder abgebrochenem Timer ein entsprechender Text angezeigt wird.
     * @param message Der Text, welcher in der Notification angezeigt werden soll.
     */
    private void adjustNotification(String message){
        Notification notification = notificationHelper.createNotification(getString(R.string.notif_title),
                message,
                R.drawable.ic_launcher,
                notificationHelper.createContentIntent(TimerActivity.class));
        notificationHelper.showNotification(NotificationHelper.NOTIFICATION_ID_TIMER_SERVICE, notification);
    }

    /**
     * Dieser Broadcast wird verschickt, wenn sich der Timer updated.
     * @param remainingTime Die Zeit die noch auf dem Timer verbleibt.
     */
    private void broadcastTimerUpdate(int remainingTime)
    {
        Intent intent = TimerBroadcastReceiver.getUpdateIntent(remainingTime);
        sendBroadcast(intent);
    }

    /**
     * Dieser Broadcast wird verschickt, wenn der Timer komplett abgelaufen ist.
     */
    private void broadcastTimerFinished()
    {
        Intent intent = TimerBroadcastReceiver.getEndIntent();
        sendBroadcast(intent);
    }

    /**
     * Dieser Broadcast wird verschickt, wenn der Timer vor Ablauf abgebrochen wird
     */
    private void broadcastTimerCancelled()
    {
        Intent intent = TimerBroadcastReceiver.getCancelledIntent();
        sendBroadcast(intent);
    }

    /**
     * Über diese Method wird der entsprechende boolean Wert in den SharedPreferences abgelegt.
     * Diese Daten können später von der Activity benutzt werden, um Buttons und andere Elemente
     * richtig darzustellen, je nachdem ob der Timer läuft oder nicht.
     * @param isRunning boolean Wert, welcher angibt ob der Timer gerade läuft oder nicht.
     */
    private void setTimerRunning(boolean isRunning){
        Log.d("PREFS", "Put Value in prefs");
        preferenceHelper.put(PreferenceHelper.TIMER_RUNNING_KEY, isRunning);
    }
}