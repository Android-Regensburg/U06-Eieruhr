package de.ur.mi.android.tasks.timer.prefs;

public enum TimerState {
    IDLE(false),
    RUNNING(true);

    public final boolean value;

    TimerState(boolean value) {
        this.value = value;
    }

    static TimerState fromBoolean(boolean value) {
        if (value) {
            return TimerState.RUNNING;
        }
        return TimerState.IDLE;
    }
}
