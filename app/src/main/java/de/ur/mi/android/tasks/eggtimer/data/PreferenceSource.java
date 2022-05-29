package de.ur.mi.android.tasks.eggtimer.data;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class PreferenceSource<T> {

    private final SharedPreferences preferences;

    public PreferenceSource(Context context, String preferenceKey) {
        this.preferences = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
    }

    protected SharedPreferences getPreferences() {
        return preferences;
    }

    public abstract T retrieve();

    public abstract void store(T t, PreferenceSourceChangeListener<T> l);

    public interface PreferenceSourceChangeListener<T> {
        void onChange(T t);
    }

}