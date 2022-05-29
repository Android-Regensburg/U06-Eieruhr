package de.ur.mi.android.tasks.eggtimer.data.timer;

import android.content.Context;

public class TimerStateRepository {

    private final TimerStateSource source;

    public TimerStateRepository(Context context) {
        this.source = new TimerStateSource(context);
    }

    public void setTimerState(TimerState state, TimerStateChangeListener listener) {
        source.store(state, timerState -> {
            listener.onChange(getTimerState());
        });
    }

    public TimerState getTimerState() {
        return source.retrieve();
    }

    public interface TimerStateChangeListener {
        void onChange(TimerState state);
    }
}