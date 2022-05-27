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
import de.ur.mi.android.tasks.timer.notifications.NotificationHelper;
import de.ur.mi.android.tasks.timer.prefs.TimerState;
import de.ur.mi.android.tasks.timer.prefs.TimerStateStorage;
import de.ur.mi.android.tasks.timer.service.TimerService;

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
        setupNotifications();
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

    /**
     * Erstellt einen Notification-Channel, damit die App Notifications anzeigen kann.
     */
    private void setupNotifications() {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.createNotificationChannel();
    }

    //endregion

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonStates();
    }

    /**
     * passt die Buttons so an, dass sie den richtigen Status haben, je nachdem ob gerade ein Timer läuft oder nicht.
     */
    private void updateButtonStates() {
        TimerState currentState = stateStorage.getTimerState();
        btnStartTimer.setEnabled(!currentState.value);
        btnStopTimer.setEnabled(currentState.value);
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


    /**
     * Stoppt den Timer, indem der Service beendet wird.
     */
    private void stopTimer() {
        stopService(new Intent(this, TimerService.class));
    }

    /**
     * Überprüft, ob alle Angaben korrekt sind und startet den Timer, wenn dies der Fall ist.
     * Andernfalls wird ein Toast angezeigt mit der Information die Eingaben zu berichtigen.
     */
    private void startTimer() {
        if (checkInputs()) {
            int[] setTime = getSetTime();
            int timeInSeconds = (setTime[0] * 60 * 60) + (setTime[1] * 60) + (setTime[2]);
            if (timeInSeconds == 0) {
                Toast.makeText(this, getString(R.string.toast_time_not_set), Toast.LENGTH_SHORT).show();
            } else {
                startTimerForTime(timeInSeconds);
            }
            resetEdits();
        } else {
            Toast.makeText(this, R.string.toast_incorrect_input, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Setzt die Eingabefelder zurück.
     */
    private void resetEdits() {
        edtHours.setText("");
        edtMinutes.setText("");
        edtSeconds.setText("");
    }

    /**
     * Startet den Service und gibt dem Intent als Extra die Laufzeit des Timers in Sekunden mit.
     *
     * @param timeInSeconds Laufzeit des Timers.
     */
    private void startTimerForTime(int timeInSeconds) {
        btnStartTimer.setEnabled(false);
        btnStopTimer.setEnabled(true);
        txtTimer.setText(Timer.getFormattedStringFromInt(this, timeInSeconds));
        Intent intent = new Intent(this, TimerService.class);
        intent.putExtra(Timer.TIME_KEY, timeInSeconds);
        startForegroundService(intent);
    }

    /**
     * Überprüft die Angeben der Eingabefelder.
     *
     * @return True, wenn alle Angaben korrekt sind.
     */
    private boolean checkInputs() {
        int[] setTime = getSetTime();
        int h = setTime[0], m = setTime[1], s = setTime[2];
        return h <= 99 && m <= 59 && s <= 59;
    }

    /**
     * Liest die Eingaben aus den 3 Eingabefeldern aus und liefert ein int-Array zurück, welches die
     * 3 Werte beinhaltet.
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


    /**
     * Resets the Buttons and the timer-TextView while also allowing to send a Toast to the user
     *
     * @param message The toasts message.
     */
    private void resetAndNotify(String message) {
        txtTimer.setText("");
        btnStopTimer.setEnabled(false);
        btnStartTimer.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimerUpdate(int remainingTimeInSeconds) {
        runOnUiThread(() -> txtTimer.setText(Timer.getFormattedStringFromInt(this, remainingTimeInSeconds)));
    }

    @Override
    public void onTimerFinished() {
        stateStorage.setTimerState(TimerState.IDLE);
        runOnUiThread(() -> resetAndNotify(getString(R.string.toast_timer_finished)));
    }


    @Override
    public void onTimerCancelled() {
        stateStorage.setTimerState(TimerState.IDLE);
        runOnUiThread(() -> resetAndNotify(getString(R.string.toast_timer_cancelled)));
    }
}
