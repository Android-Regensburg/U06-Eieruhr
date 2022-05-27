package de.ur.mi.android.tasks.timer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import de.mi.timer.R;
import de.ur.mi.android.tasks.timer.broadcast.TimerBroadcastListener;
import de.ur.mi.android.tasks.timer.broadcast.TimerBroadcastReceiver;
import de.ur.mi.android.tasks.timer.prefs.TimerState;
import de.ur.mi.android.tasks.timer.prefs.TimerStateStorage;

/**
 * Die MainActivity der App, über die der Timer gestartet und gestoppt wird, sowie die
 * Eingabe des Users ausgelsen.
 */
public class TimerActivity extends AppCompatActivity implements TimerBroadcastListener {

    private TextView txtTimer;
    private Button btnStartTimer, btnStopTimer;
    private EditText edtHours, edtMinutes, edtSeconds;
    private TimerBroadcastReceiver broadcastReceiver;
    private TimerStateStorage stateStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        stateStorage = new TimerStateStorage(getApplicationContext());
        initUI();
        initBroadcasts();
    }

    //region Initialization

    /**
     * Initialisiert das UI der App.
     */
    private void initUI() {
        txtTimer = findViewById(R.id.txt_timer);
        btnStartTimer = findViewById(R.id.btn_start_timer);
        btnStopTimer = findViewById(R.id.btn_stop_timer);
        edtHours = findViewById(R.id.edt_hour_input);
        edtMinutes = findViewById(R.id.edt_minute_input);
        edtSeconds = findViewById(R.id.edt_second_input);
        btnStartTimer.setOnClickListener(v -> startTimer());
        btnStopTimer.setOnClickListener(v -> stopTimer());
    }

    /**
     * Initialisiert den BroadcastReceiver um gesendete Broadcasts empfangen und verarbeiten zu können
     */
    private void initBroadcasts() {
        broadcastReceiver = new TimerBroadcastReceiver(this);
    }


    //endregion


    //region Broadcast-Management

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }


    /**
     * Registriert den BroadcastReceiver
     */
    private void registerBroadcastReceiver() {
        broadcastReceiver = new TimerBroadcastReceiver(this);
        this.registerReceiver(broadcastReceiver, TimerBroadcastReceiver.getIntentFilter());
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterBroadcastReceiver();

    }

    /**
     * Unregistriert den BroadcastReceiver
     */
    private void unregisterBroadcastReceiver() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    //endregion

    /**
     * Stoppt den Timer, indem der Service beendet wird.
     */
    private void stopTimer() {

    }

    /**
     * Überprüft, ob alle Angaben korrekt sind und startet den Timer, wenn dies der Fall ist.
     * Andernfalls wird ein Toast angezeigt mit der Information die Eingaben zu berichtigen.
     */
    private void startTimer() {
        //TODO: Wenn der Start-Button geklickt wird soll überprüft werden ob alle Inputs richtig sind
        //TODO: Ist dies der Fall, kann die Eingabe ausgelesen werden und an startTimerForTime gereicht werden
    }


    /**
     * Startet den Service und gibt dem Intent als Extra die Laufzeit des Timers in Sekunden mit.
     *
     * @param timeInSeconds Laufzeit des Timers.
     */
    private void startTimerForTime(int timeInSeconds) {
        //TODO: Starten Sie hier ihren Timer.
    }

    /**
     * Liest die Eingaben aus den 3 Eingabefeldern aus und liefert ein int-Array zurück, welches die
     * 3 Werte beinhaltet. Sollte ds Feld leer sein wird als Standard-Wert 0 zurückgegeben
     *
     * @return int-Array mit den Werten für Stunden, Minuten und Sekunden.
     */
    @SuppressLint("SetTextI18n")
    private int[] getSetTime() {
        int h, m, s;

        if (edtHours.getText().toString().isEmpty()) {
            edtHours.setText("00");
        }
        if (edtMinutes.getText().toString().isEmpty()) {
            edtMinutes.setText("00");
        }
        if (edtSeconds.getText().toString().isEmpty()) {
            edtSeconds.setText("00");
        }

        h = Integer.parseInt(edtHours.getText().toString());
        m = Integer.parseInt(edtMinutes.getText().toString());
        s = Integer.parseInt(edtSeconds.getText().toString());

        return new int[]{h, m, s};
    }



    @Override
    public void onTimerUpdate(int remainingTimeInSeconds) {
    }

    @Override
    public void onTimerFinished() {
        stateStorage.setTimerState(TimerState.IDLE);
    }


    @Override
    public void onTimerCancelled() {
        stateStorage.setTimerState(TimerState.IDLE);
    }
}
