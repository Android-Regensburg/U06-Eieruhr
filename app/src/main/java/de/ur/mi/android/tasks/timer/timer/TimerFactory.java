package de.ur.mi.android.tasks.timer.timer;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class TimerFactory {

    public static Timer createTimer(int timeInSeconds, TimerListener listener) {
        return new Timer(timeInSeconds, listener);
    }

    public interface TimerListener {

        void onTimerStarted();

        void onTimerResumed();

        void onTimerPaused();

        void onTimerStopped();

        void onTimerUpdated(int remainingTimeInSeconds);
    }

    public static class Timer {

        private static final int TICK_RATE = 1000;

        private final int targetSeconds;
        private int secondsFromPreviousScheduling;
        private final TimerListener listener;
        private long scheduledAt;
        private TimerState currentState;
        private ScheduledFuture scheduledFuture;

        public Timer(int timeInSeconds, TimerListener listener) {
            this.targetSeconds = timeInSeconds;
            this.listener = listener;
            secondsFromPreviousScheduling = 0;
            currentState = TimerState.IDLE;
        }

        public void start() {
            scheduleTicker();
            listener.onTimerStarted();
        }

        public void resume() {
            scheduleTicker();
            listener.onTimerResumed();
        }

        public void pause() {
            scheduledFuture.cancel(true);
            secondsFromPreviousScheduling += (int) ((System.currentTimeMillis() - scheduledAt) / 1000);
            currentState = TimerState.PAUSED;
            listener.onTimerPaused();
        }

        public void stop() {
            scheduledFuture.cancel(true);
            currentState = TimerState.IDLE;
            listener.onTimerStopped();
        }

        public int getRemainingTime() {
            int secondsSinceSchedulerStarted = (int) ((System.currentTimeMillis() - scheduledAt) / 1000);
            return targetSeconds - secondsFromPreviousScheduling - secondsSinceSchedulerStarted;
        }


        private void scheduleTicker() {
            Log.d("QuickTimer", "in: scheduleTimer");
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::update, TICK_RATE, TICK_RATE, TimeUnit.MILLISECONDS);
            scheduledAt = System.currentTimeMillis();
            currentState = TimerState.RUNNING;
        }

        private void update() {
            Log.d("QuickTimer", "in: update");
            int secondsSinceSchedulerStarted = (int) ((System.currentTimeMillis() - scheduledAt) / 1000);
            int remainingTimeInSeconds = targetSeconds - secondsFromPreviousScheduling - secondsSinceSchedulerStarted;
            if (remainingTimeInSeconds <= 0) {
                stop();
            } else {
                listener.onTimerUpdated(remainingTimeInSeconds);
            }
        }

    }
}
