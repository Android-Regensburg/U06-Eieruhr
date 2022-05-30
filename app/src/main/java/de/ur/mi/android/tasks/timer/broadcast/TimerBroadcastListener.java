package de.ur.mi.android.tasks.timer.broadcast;

/**
 * Interface zur Kommunikation von Timer-Events an registrierte Listener. Über die Methoden werden
 * angeschlossenen Listener vom BroadcastReceiver über eingegangene Status-Updates des Timers informiert.
 */
public interface TimerBroadcastListener {


    void onTimerStarted();

    void onTimerResumed();

    void onTimerPaused();

    void onTimerStopped();

    void onTimerUpdated(int remainingTimeInSeconds);

}