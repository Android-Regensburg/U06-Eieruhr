package de.ur.mi.android.tasks.timer.prefs;

import android.content.Context;

/**
 * Mit Hilfe dieser Klasse können Sie den aktuellen Zustand des Timers, abgebildet über die Werte
 * des Enums TimerState (IDLE: Timer läuft nicht, RUNNING: Timer läuft) innerhalb der Anwendung
 * persistieren. Dazu werden die SharedPrefrences [1] verwendet, eine sehr einfache Möglichkeit, wenig
 * komplexe Daten, wie z.B. Einstellungen, sitzungsübergreifend zu speichern. In dieser Anwendung
 * verwenden wir diesen Speicher um sicherzustellen, dass der aktuelle Zustand des Timers auch dann
 * sicher abgerufen werden kann, wenn die Anwendung in den Hintergrund verschoben wurde.
 *
 * Zur Verwendung dieser Klasse instanziieren Sie ein neues Objekt und übergeben dabei den Kontext
 * der laufenden Anwendung. Über die öffentlichen Methoden können Sie den Zustand des Timers auslesen
 * und setzen.
 *
 * [1]: https://developer.android.com/reference/android/content/SharedPreferences
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
        /*
         * Achtung! Das Speichern des Timer-Zustands in den SharedPreferences erfolgt hier asynchron,
         * da die Operation mit der apply-Methode [1] abgeschlossen wird. D.h., das zum Zeitpunkt,
         * an dem die Methode beendet wird und die Anwendung nach der aufrufenden Stelle im Code
         * fortgesetzt wird, kann nicht zwingend davon ausgegangen werden, dass der Zustand bereits
         * persistiert wurde. Wird ignorieren diesen Umstand hier. Korrekterweise sollten Sie Ihren
         * Code aber anders gestalten, insbesondere, wenn weitere Aktionen im Code vom erfolgreichen
         * Speichern des Werts abhängen. In diesen Fällen sollten Sie den Abschluss der Operation
         * abwarten (über einen entsprechenden Listener auf den SharedPreferences) und erst dann die
         * aufrufende telle asynchron über einen Listener oder einen ähnlichen Mechanismus informieren.
         *
         * [1]: https://developer.android.com/reference/android/content/SharedPreferences.Editor#apply()
         */
        context.getSharedPreferences(TIMER_PREFERENCES_KEY, Context.MODE_PRIVATE).edit().putBoolean(TIMER_STATE_KEY, state.value).apply();
    }

    /**
     * Ändert den gespeicherten Zustand des Timers auf den übergebenen Wert.
     *
     * @param state Der neue Zustand des Timers (IDLE oder RUNNING)
     */
    public void setTimerState(TimerState state) {
        this.state = state;
        setStoredValue(state);
    }

    /**
     * Gibt den gespeicherten Zustand des Timers zurück.
     *
     * @return Der aktuelle Zustand des Timers (IDLE oder RUNNING)
     */
    public TimerState getTimerState() {
        return state;
    }
}
