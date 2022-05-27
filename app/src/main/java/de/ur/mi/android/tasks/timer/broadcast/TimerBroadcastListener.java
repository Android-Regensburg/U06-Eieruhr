package de.ur.mi.android.tasks.timer.broadcast;

/**
 * Interface zur Informierung der TimerActivity über empfangene Broadcasts
 */
public interface TimerBroadcastListener {

    void onTimerUpdate(int remainingTimeInSeconds);

    void onTimerFinished();

    void onTimerCancelled();

}