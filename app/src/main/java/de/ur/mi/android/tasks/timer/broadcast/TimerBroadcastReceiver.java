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

    // Action-Name für Broadcast zur Kommunikation der verbleibenden Zeit des laufenden Timers
    private static final String TIMER_UPDATE = "de.ur.mi.android.task.timer.TIMER_UPDATE";
    // Action-Name für Broadcast zur Kommunikation des erfolgreichen Durchlaufs des Timers
    private static final String TIMER_FINISHED = "de.ur.mi.android.task.timer.TIMER_FINISHED";
    // Action-Name für Broadcast zur Kommunikation des vorzeitigen Abbruchs des Timers
    private static final String TIMER_CANCELLED = "de.ur.mi.android.task.timer.TIMER_CANCELLED";
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
            // Broadcast: Verbleibende Zeit des laufenden Timers hat sich geändert
            case TIMER_UPDATE:
                int remainingTimeInSeconds = intent.getExtras().getInt(REMAINING_TIME_IN_SECONDS);
                listener.onTimerUpdate(remainingTimeInSeconds);
                break;
            // Broadcast: Laufender Timer ist vollständig abgelaufen
            case TIMER_FINISHED:
                listener.onTimerFinished();
                break;
            // Broadcast: Laufender Timer wurde vorzeitig abgebrochen
            case TIMER_CANCELLED:
                listener.onTimerCancelled();
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
        filter.addAction(TimerBroadcastReceiver.TIMER_UPDATE);
        filter.addAction(TimerBroadcastReceiver.TIMER_FINISHED);
        filter.addAction(TimerBroadcastReceiver.TIMER_CANCELLED);
        return filter;
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

    /**
     * Erzeugt einen Intent, über den ein Broadcast versendet werden kann, der über den erfolgreichen
     * Abschluss des Timers informiert.
     */
    public static Intent getEndIntent() {
        Intent intent = new Intent();
        intent.setAction(TIMER_FINISHED);
        return intent;
    }

    /**
     * Erzeugt einen Intent, über den ein Broadcast versendet werden kann, der über den vorzeitigen
     * Abbruch des Timers informiert.
     */
    public static Intent getCancelledIntent() {
        Intent intent = new Intent();
        intent.setAction(TIMER_CANCELLED);
        return intent;
    }

}
