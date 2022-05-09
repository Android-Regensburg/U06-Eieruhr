package de.ur.mi.android.tasks.eggtimer;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.ur.mi.android.tasks.eggtimer.broadcast.TimerBroadcastListener;

public class Timer implements Runnable {

    public static String TIME_KEY = "TIME_KEY";
    private boolean isRunning;
    private int time;
    private static final int TICK_RATE = 1000;
    private ScheduledFuture scheduledFuture;
    private TimerBroadcastListener listener;

    public Timer (TimerBroadcastListener listener)
    {
        this.listener = listener;
        isRunning = false;
    }

    public void setTime(int time){
        this.time = time;
    }

    public void start()
    {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(this, TICK_RATE, TICK_RATE, TimeUnit.MILLISECONDS);
        isRunning = true;
    }

    public void stop(){
        if (isRunning){
            scheduledFuture.cancel(true);
            isRunning = false;
            listener.onTimerCancelled();
        }
    }

    @Override
    public void run() {
        time--;
        if (time > 0){
            listener.onTimerUpdate(time);
        }
        else {
            listener.onTimerFinished();
            scheduledFuture.cancel(true);
        }
    }
}
