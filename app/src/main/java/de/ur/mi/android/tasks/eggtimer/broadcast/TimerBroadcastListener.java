package de.ur.mi.android.tasks.eggtimer.broadcast;

/**
 * Interface zur Informierung der TimerActivity über empfangene Broadcasts
 */
public interface TimerBroadcastListener {

    void onTimerUpdate(int remainingTimeInSeconds);

    void onTimerFinished();

}