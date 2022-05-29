package de.ur.mi.android.tasks.eggtimer.data.timer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import de.ur.mi.android.tasks.eggtimer.data.PreferenceSource;

public class TimerStateSource extends PreferenceSource<TimerState> {

    private static final String TIMER_PREFERENCES_KEY = "de.ur.mi.android.timer.preferences";
    private static final String TIMER_STATE_KEY = "de.ur.mi.android.timer.preferences.state";

    public TimerStateSource(Context context) {
        super(context, TIMER_PREFERENCES_KEY);
    }

    @Override
    public TimerState retrieve() {
        boolean storedValue = getPreferences().getBoolean(TIMER_STATE_KEY, false);
        return TimerState.fromBoolean(storedValue);
    }

    @Override
    public void store(TimerState state, PreferenceSourceChangeListener<TimerState> listener) {
        getPreferences().registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals(TIMER_STATE_KEY)) {
                listener.onChange(retrieve());
            }
        });
        getPreferences().edit().putBoolean(TIMER_STATE_KEY, state.value).apply();
    }
}
