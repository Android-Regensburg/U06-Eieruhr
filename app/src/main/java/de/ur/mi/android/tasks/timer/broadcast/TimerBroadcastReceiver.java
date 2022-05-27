package de.ur.mi.android.tasks.timer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Der BroadcastReceiver empfängt Broadcasts anderer Android-Apps oder, in diesem Fall, eines Hintergrundservices unserer App.
 * Hier soll bei Änderung oder Ablauf des Timers später die TimerActivity informiert werden.
 */
public class TimerBroadcastReceiver extends BroadcastReceiver {

    /**
     * Konstanten zur Unterscheidung der Broadcast-Nachrichten
     */
    private static final String TIMER_UPDATE = "de.ur.mi.android.task.timer.TIMER_UPDATE";
    private static final String TIMER_FINISHED = "de.ur.mi.android.task.timer.TIMER_FINISHED";
    private static final String TIMER_CANCELLED = "de.ur.mi.android.task.timer.TIMER_CANCELLED";
    private static final String REMAINING_TIME_IN_SECONDS = "REMAINING_TIME_IN_SECONDS";


    /**
     * Über den TimerBroadcastListener kann die Activity informiert werden
     */
    private final TimerBroadcastListener listener;

    public TimerBroadcastReceiver(TimerBroadcastListener listener) {
        this.listener = listener;
    }



    /**
     * Die onReceive-Callback-Methode wird aufgerufen, wenn Broadcasts mit den Aktionsbezeichnern empfangen werden,
     * für die die Activity sich registriert. Je nach Action wird die jeweilige Callback-Methode des EggTimerBroadcastListeners gecalled
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case TIMER_UPDATE:
                int remainingTimeInSeconds = intent.getExtras().getInt(REMAINING_TIME_IN_SECONDS);
                listener.onTimerUpdate(remainingTimeInSeconds);
                break;
            case TIMER_FINISHED:
                listener.onTimerFinished();
                break;
            case TIMER_CANCELLED:
                listener.onTimerCancelled();
                break;
        }
    }

    /**
     * Durch IntentFilter kann bestimmt wird, für welche Art von Broadcasts die App bzw. die Activity sich interessiert
     */
    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerBroadcastReceiver.TIMER_UPDATE);
        filter.addAction(TimerBroadcastReceiver.TIMER_FINISHED);
        filter.addAction(TimerBroadcastReceiver.TIMER_CANCELLED);
        return filter;
    }

    /**
     * Erzeugt einen Intent, der diesen BroadcastReceiver darüber informiert, wieviele Sekunden im
     * Timer noch verbleiben.
     */
    public static Intent getUpdateIntent(int remainingTimeInSeconds) {
        Intent intent = new Intent();
        intent.setAction(TIMER_UPDATE);
        intent.putExtra(REMAINING_TIME_IN_SECONDS,remainingTimeInSeconds);
        return intent;
    }

    /**
     * Erzeugt einen Intent, der diesen BroadcastReceiver darüber informiert, dass die Zeit im Timer
     * vollständig abgelaufen ist.
     */
    public static Intent getEndIntent() {
        Intent intent = new Intent();
        intent.setAction(TIMER_FINISHED);
        return intent;
    }

    /**
     * Erzeugt einen Intent, der diesen BroadcastReceiver darüber informiert, dass der Timer
     * abgebrochen wurde.
     */
    public static Intent getCancelledIntent() {
        Intent intent = new Intent();
        intent.setAction(TIMER_CANCELLED);
        return intent;
    }

}
