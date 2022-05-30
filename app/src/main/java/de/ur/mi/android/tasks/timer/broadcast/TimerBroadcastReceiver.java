package de.ur.mi.android.tasks.timer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Dieser Broadcast-Receiver empfängt Broadcasts anderer Stellen unserer Anwendung. Konkret gehen hier
 * die Statusmeldungen des Timers ein, die aus dem Hintergrundservice verschickt werden. Der Receiver
 * informiert angeschlossene Listener (TimerBroadcastListener) auf Basis dieser Broadcast, wenn der
 * laufende Timer vollständig abgelaufen ist oder vorzeitig abgebrochen wurde. Zusätzlich werden die
 * regelmäßigen Updates zur noch verbleibenden Zeit des Times weitergegeben.
 */
public class TimerBroadcastReceiver extends BroadcastReceiver {

    // Action-Name für Broadcast zur Kommunikation der erfolgreichen Starten des Timers
    private static final String TIMER_START = "de.ur.mi.android.task.timer.TIMER_START";
    // Action-Name für Broadcast zur Kommunikation der erfolgreichen Pausierens des Timers
    private static final String TIMER_PAUSE = "de.ur.mi.android.task.timer.TIMER_PAUSE";
    // Action-Name für Broadcast zur Kommunikation der erfolgreichen Fortsetzen des Timers
    private static final String TIMER_RESUME = "de.ur.mi.android.task.timer.TIMER_RESUME";
    // Action-Name für Broadcast zur Kommunikation der erfolgreichen Stoppens des Timers
    private static final String TIMER_STOP = "de.ur.mi.android.task.timer.TIMER_STOP";
    // Action-Name für Broadcast zur Kommunikation der verbleibenden Zeit des laufenden Timers
    private static final String TIMER_UPDATE = "de.ur.mi.android.task.timer.TIMER_UPDATE";
    // Schlüssel zum Speichern/Auslesen der verbleibenden Zeit im entsprechenden Broadcast
    private static final String REMAINING_TIME_IN_SECONDS = "REMAINING_TIME_IN_SECONDS";

    // Listener, der vom Receiver über die eingehenden Status-Updates informiert werden soll
    private final TimerBroadcastListener listener;

    /**
     * Erzeugt einen neuen BroadcastReceiver. Der übergebene Listener wird im Anschluss über die
     * als Broadcasts eingehenden Status-Updates des Timers informiert.
     *
     * @param listener Listener, der vom Receiver über Status-Updates informiert werden soll.
     */
    public TimerBroadcastReceiver(TimerBroadcastListener listener) {
        this.listener = listener;
    }


    /**
     * Hier werden die eingehenden Broadcast, für die dieser Receiver ausgelegt ist
     * (siehe getIntentFilter) abgefangen. Die Methode wird jedes mal aufgerufen, wenn ein passender
     * Broadcast verschickt wurde. Über den übergebenen Intent kann die Art des Broadcasts und die
     * ggf. enthaltenden Informationen ausgelesen werden.
     *
     * @param context Context, in dem der Receiver ausgeführt wird
     * @param intent  Intent, der den empfangenen Broadcast repräsentiert
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case TIMER_START:
                listener.onTimerStarted();
                break;
            case TIMER_PAUSE:
                listener.onTimerPaused();
                break;
            case TIMER_RESUME:
                listener.onTimerResumed();
                break;
            case TIMER_STOP:
                listener.onTimerStopped();
                break;
            case TIMER_UPDATE:
                int remainingTimeInSeconds = intent.getExtras().getInt(REMAINING_TIME_IN_SECONDS);
                listener.onTimerUpdated(remainingTimeInSeconds);
                break;
        }
    }

    /**
     * Die Methode liefert einen IntentFilter zurück, über den beim Registrieren des Receivers in
     * der App (Vgl. TimerActivity, Methode registerBroadcastReceiver) festgelegt werden kann, welche
     * Broadcasts der Receiver empfangen soll. So stellen wir sicher, dass dieser Receiver später nur
     * auf die von uns versendeten Broadcasts reagiert, die über den Zustand des Timers informieren.
     */
    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerBroadcastReceiver.TIMER_START);
        filter.addAction(TimerBroadcastReceiver.TIMER_PAUSE);
        filter.addAction(TimerBroadcastReceiver.TIMER_RESUME);
        filter.addAction(TimerBroadcastReceiver.TIMER_STOP);
        filter.addAction(TimerBroadcastReceiver.TIMER_UPDATE);
        return filter;
    }


    public static Intent getStartIntent() {
        Intent intent = new Intent();
        intent.setAction(TIMER_START);
        return intent;
    }


    public static Intent getPauseIntent() {
        Intent intent = new Intent();
        intent.setAction(TIMER_PAUSE);
        return intent;
    }

    public static Intent getResumeIntent() {
        Intent intent = new Intent();
        intent.setAction(TIMER_PAUSE);
        return intent;
    }


    /**
     * Erzeugt einen Intent, über den ein Broadcast versendet werden kann, der über den erfolgreichen
     * Abschluss des Timers informiert.
     */
    public static Intent getStopIntent() {
        Intent intent = new Intent();
        intent.setAction(TIMER_STOP);
        return intent;
    }

    /**
     * Erzeugt einen Intent, über den ein Broadcast mit der verbleibenden Zeit des laufenden Timers
     * versendet werden kann. Die statische Methode erlaubt anderen Stellen der App eine entsprechende
     * Nachricht zu versenden, die dann von den registrierten Receivern abgefangen und verarbeitet werden
     * kann.
     *
     * @param remainingTimeInSeconds Die verbleibende Zeit des Timers in Sekunden.
     */
    public static Intent getUpdateIntent(int remainingTimeInSeconds) {
        Intent intent = new Intent();
        intent.setAction(TIMER_UPDATE);
        intent.putExtra(REMAINING_TIME_IN_SECONDS, remainingTimeInSeconds);
        return intent;
    }


}
