package de.ur.mi.android.tasks.timer.prefs;

import android.content.Context;

/**
 * Diese Klasse kümmert sich um das abspeichern des Timer-Zustands.
 */
public class TimerStateStorage {

    private static final String TIMER_PREFERENCES_KEY = "de.ur.mi.android.timer.preferences";
    private static final String TIMER_STATE_KEY = "de.ur.mi.android.timer.preferences.state";

    private final Context context;
    private TimerState state;

    public TimerStateStorage(Context context) {
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


    /**
     * Verändert den state des Timers und speichert den Wert anschließend ab.
     * @param state Der neue state des Timers.
     */
    public void setTimerState(TimerState state) {
        this.state = state;
        setStoredValue(state);
    }

    /**
     * Liefert den aktuellen Stand des Timers.
     * @return den gesetzten TimerState.
     */
    public TimerState getTimerState( ) {
        return state;
    }
}
