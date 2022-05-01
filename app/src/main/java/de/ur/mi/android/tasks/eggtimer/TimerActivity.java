package de.ur.mi.android.tasks.eggtimer;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

import de.mi.eggtimer.R;
import de.ur.mi.android.tasks.eggtimer.broadcast.TimerBroadcastListener;

public class TimerActivity extends AppCompatActivity implements TimerBroadcastListener {

    private TextView txtTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        initUI();


    }

    private void initUI() {
        txtTimer = findViewById(R.id.txt_timer);
    }


    /**
     * Nutzen Sie diese Methode, um die noch verbleibende Zeit des Timers im User Interface dar-
     * zustellen. Die angegebenen Sekunden werden automatisch in das richtige Anzeigeformat über-
     * tragen und korrekt im TextView dargstellt.
     */
    private void updateTimerValue(int remainingSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        int hours = remainingSeconds / 60 / 60;
        //Um die weitere Berechnung nicht zu beeinfluss werden die berechneten Stunden von der Zeit abgezogen
        //Andernfalls würde der Timer zum Beispiel 2 Stunden und 120 Minuten anzeigen
        //Da diese Methode keinen Wert zurückgibt besteht keine Gefahr, den Timer durch diese Manipulation zu verändern
        remainingSeconds = remainingSeconds - (hours*60*60);
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        String currentTimer = getString(R.string.txt_timer_template).replace("$HOURS", df.format(hours))
                .replace("$MINUTES", df.format(minutes))
                .replace("$SECONDS", df.format(seconds));
        txtTimer.setText(currentTimer);
    }

    @Override
    public void onTimerUpdate(int remainingTimeInSeconds) {

    }

    @Override
    public void onTimerFinished() {

    }
}
