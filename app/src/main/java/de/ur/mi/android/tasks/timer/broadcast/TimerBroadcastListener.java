package de.ur.mi.android.tasks.timer.broadcast;

/**
 * Interface zur Kommunikation von Timer-Events an registrierte Listener. Über die Methoden werden
 * angeschlossene Listener vom BroadcastReceiver über eingegangenen Status-Updates des Timers informiert.
 */
public interface TimerBroadcastListener {

    /**
     * Informiert Listener regelmäßig über die verbleibende Zeit des laufenden Timers.
     * @param remainingTimeInSeconds Die noch verbleibende Zeit in Sekunden.
     */
    void onTimerUpdate(int remainingTimeInSeconds);

    /**
     * Informiert Listener, wenn der Timer vollständig abgelaufen ist.
     */
    void onTimerFinished();

    /**
     * Informiert Listener, wenn der Timer vorzeitig abgebrochen wurde.
     */
    void onTimerCancelled();

}