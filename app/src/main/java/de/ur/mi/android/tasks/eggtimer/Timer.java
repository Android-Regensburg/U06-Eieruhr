package de.ur.mi.android.tasks.eggtimer;

import android.content.Context;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.mi.eggtimer.R;
import de.ur.mi.android.tasks.eggtimer.broadcast.TimerBroadcastListener;

/**
 * Diese Klasse stellt den Timer dar, welche die Hauptfunktion der App bildet.
 */
public class Timer implements Runnable {

    public static String TIME_KEY = "TIME_KEY";
    private boolean isRunning;
    private int time;
    private static final int TICK_RATE = 1000;
    private ScheduledFuture scheduledFuture;
    private TimerBroadcastListener listener;

    public Timer (TimerBroadcastListener listener)
    {
        this.listener = listener;
        isRunning = false;
    }

    /**
     * Über diese Methode kann festgelegt werden, wie lange der Timer läuft
     * @param time Laufzeit des Timers, in Sekunden.
     */
    public void setTime(int time){
        this.time = time;
    }

    /**
     * Über diese Methode wird der Timer gestartet, es wird ein Sheduled Executor erzeugt, welcher
     * anschließdend die entprechende Run-Methode des übergebenen Runnables in reglemäßigen
     * Abständen (hier: jede Sekunde) ausführt. Der ScheduledExecutor liefert uns ein Objekt des Typs
     * SheduledFuture, welches jederzeit beendet werden kann, um die weitere Ausführung des
     * Executors abzubrechen.
     */
    public void start()
    {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(this, TICK_RATE, TICK_RATE, TimeUnit.MILLISECONDS);
        isRunning = true;
    }

    /**
     * Hält den Timer an und cancelled das SheduledFuture.
     */
    public void stop(){
        if (isRunning){
            scheduledFuture.cancel(true);
            isRunning = false;
            listener.onTimerCancelled();
        }
    }

    /**
     * Die run-Methode setzt bei jeder Ausführung die Zeit um 1 herab, wenn sie 0 erreicht wird die
     * weitere Ausführung abgebrochen.
     */
    @Override
    public void run() {
        time--;
        if (time > 0){
            listener.onTimerUpdate(time);
        }
        else {
            listener.onTimerFinished();
            scheduledFuture.cancel(true);
        }
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
