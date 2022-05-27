package de.ur.mi.android.tasks.eggtimer.prefs;

import android.content.Context;

public class TimerStateStorage {

    public static final String TIMER_PREFERENCES_KEY = "de.ur.mi.android.timer.preferences";
    public static final String TIMER_STATE_KEY = "de.ur.mi.android.timer.preferences.state";

    private final Context context;
    private TimerState state;

    private TimerStateStorage(Context context) {
        this.context = context;
        this.state = getStoredState();
    }

    private TimerState getStoredState() {
        boolean storedValue = context.getSharedPreferences(TIMER_PREFERENCES_KEY, Context.MODE_PRIVATE).getBoolean(TIMER_STATE_KEY, false);
        return TimerState.fromBoolean(storedValue);
    }

    private void setStoredValue(TimerState state) {
        context.getSharedPreferences(TIMER_PREFERENCES_KEY, Context.MODE_PRIVATE).edit().putBoolean(TIMER_STATE_KEY, state.value).apply();
    }

    public static TimerStateStorage fromContext(Context context) {
        return new TimerStateStorage(context);
    }

    public void setTimerState(TimerState state) {
        this.state = state;
        setStoredValue(state);
    }

    public TimerState getTimerState( ) {
        return state;
    }
}
