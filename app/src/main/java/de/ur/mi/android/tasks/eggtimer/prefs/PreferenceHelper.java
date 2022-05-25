package de.ur.mi.android.tasks.eggtimer.prefs;

import android.content.ContentProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Diese Klasse kümmert sich darum, dass über die SharedPreferences abgespeichert werden kann,
 * ob der Timer gerade läuft oder nicht.
 */
public class PreferenceHelper {

    public static final String TIMER_RUNNING_KEY = "de.ur.mi.android.timer.TIMER.RUNNING.KEY";

    private SharedPreferences preferences;
    private Context context;

    public PreferenceHelper(Context context){
        this.context = context;
        initPreferences();
    }

    /**
     * Diese Methode liefert die Default SahredPreferences für diese App.
     */
    private void initPreferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Über diese Methode kann ein boolean Wert in den SharedPreferences gespeichert werden
     * @param key Der Key, über den auf den Wert zugegriffen werden kann.
     * @param value Der Wert in den SharedPreferences.
     */
    public void put(String key, boolean value){
        preferences.edit().putBoolean(key, value).apply();
    }

    /**
     * Über diese Methode kann ein boolean Wert aus den SharedPrefernces ausgelesen werden.
     * @param key Der Key, über den auf den Wert zugegriffen werden kann.
     * @param defaultValue Der Standardwert, falls in den SharedPreferences kein Wert zu dem
     *                     angegebenen Key liegt.
     * @return Den Wert aus den SharedPreferences, bei nichtvorhanden den dafaultValue.
     */
    public boolean getBoolean(String key, boolean defaultValue){
        return preferences.getBoolean(key, defaultValue);
    }

}
