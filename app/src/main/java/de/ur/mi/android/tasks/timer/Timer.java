package de.ur.mi.android.tasks.timer;

import android.content.Context;

import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.mi.timer.R;
import de.ur.mi.android.tasks.timer.broadcast.TimerBroadcastListener;

/**
 * Diese Klasse stellt den Timer dar, welche die Hauptfunktion der App bildet.
 */
public class Timer implements Runnable {

    public static String TIME_KEY = "TIME_KEY";

    public Timer ()
    {
        //TODO: Passen sie den Konstruktor so an, dass er sich für die Verwendung eignet.
    }


    /**
     * Die run-Methode setzt bei jeder Ausführung die Zeit um 1 herab, wenn sie 0 erreicht wird die
     * weitere Ausführung abgebrochen.
     */
    @Override
    public void run() {
        //TODO Implementieren Sie hier die Funktionalität des Timers
    }

    /**
     * Wandelt die restliche Laufzeit des Timers in einen String um und formatiert die Anzeige so,
     * dass sie das Muster ##:##:## hat
     */
    public static String getFormattedStringFromInt(Context context, int remainingSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        int hours = remainingSeconds / 60 / 60;
        remainingSeconds = remainingSeconds - (hours * 60 * 60);
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        return context.getString(R.string.txt_timer_template).replace("$HOURS", df.format(hours))
                .replace("$MINUTES", df.format(minutes))
                .replace("$SECONDS", df.format(seconds));
    }
}
