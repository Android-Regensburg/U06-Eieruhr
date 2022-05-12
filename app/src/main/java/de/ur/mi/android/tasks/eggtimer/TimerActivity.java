package de.ur.mi.android.tasks.eggtimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

import de.mi.eggtimer.R;
import de.ur.mi.android.tasks.eggtimer.broadcast.TimerBroadcastListener;
import de.ur.mi.android.tasks.eggtimer.broadcast.TimerBroadcastReceiver;
import de.ur.mi.android.tasks.eggtimer.notifications.NotificationHelper;
import de.ur.mi.android.tasks.eggtimer.service.TimerService;

public class TimerActivity extends AppCompatActivity implements TimerBroadcastListener {

    private TextView txtTimer;
    private Button btnStartTimer, btnStopTimer;
    private EditText edtHours, edtMinutes, edtSeconds;
    private TimerBroadcastReceiver broadcastReceiver;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        initUI();
        initData();
        setupNotifications();
    }

    private void setupNotifications() {
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.createNotificationChannel();
    }

    private void initData() {
        broadcastReceiver = new TimerBroadcastReceiver(this);
        timer = new Timer(TimerActivity.this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adjustOnResume();
    }

    private void adjustOnResume() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!prefs.getBoolean(Timer.TIME_KEY, false)){
            btnStartTimer.setEnabled(true);
            btnStopTimer.setEnabled(false);
        }
    }

    private void registerBroadcastReceiver() {
        broadcastReceiver = new TimerBroadcastReceiver(this);
        this.registerReceiver(broadcastReceiver, TimerBroadcastReceiver.getIntentFilter());
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterBroadcastReceiver();

    }

    private void unregisterBroadcastReceiver() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private void initUI() {
        txtTimer = findViewById(R.id.txt_timer);
        btnStartTimer = findViewById(R.id.btn_start_timer);
        btnStopTimer = findViewById(R.id.btn_stop_timer);
        edtHours = findViewById(R.id.edt_hour_input);
        edtMinutes = findViewById(R.id.edt_minute_input);
        edtSeconds = findViewById(R.id.edt_second_input);
        //btnStopTimer.setId(2131230812);
        btnStartTimer.setOnClickListener(v -> {
            startTimer();
        });
        btnStopTimer.setOnClickListener(v -> {
            stopTimer();
        });
    }

    private void stopTimer() {
        stopService(new Intent(this, TimerService.class));
    }

    private void startTimer() {
        if (checkInputs()) {
            int[] setTime = getSetTime();
            int timeInSeconds = (setTime[0] * 60 * 60) + (setTime[1] * 60) + (setTime[2]);
            if (timeInSeconds == 0) {
                Toast.makeText(this, "Zeit ist 0", Toast.LENGTH_SHORT).show();
            } else {
                startTimerForTime(timeInSeconds);
                btnStartTimer.setEnabled(false);
                btnStopTimer.setEnabled(true);
            }
            resetEdits();
        } else {
            Toast.makeText(this, R.string.toast_incorrect_input, Toast.LENGTH_SHORT).show();
        }
    }

    private void resetEdits() {
        edtHours.setText("");
        edtMinutes.setText("");
        edtSeconds.setText("");
    }

    private void startTimerForTime(int timeInSeconds) {
        updateTimerValue(timeInSeconds);
        Intent intent = new Intent(this, TimerService.class);
        intent.putExtra(Timer.TIME_KEY, timeInSeconds);
        startForegroundService(intent);
    }

    private boolean checkInputs() {
        int[] setTime = getSetTime();
        int h = setTime[0], m = setTime[1], s = setTime[2];
        return h <= 99 && m <= 59 && s <= 59;
    }

    @SuppressLint("SetTextI18n")
    private int[] getSetTime() {
        int h = 0, m = 0, s = 0;

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
     * Updates the value of the TextView displaying the time currently left on the timer.
     */
    private void updateTimerValue(int remainingSeconds) {
        DecimalFormat df = new DecimalFormat("00");
        int hours = remainingSeconds / 60 / 60;
        remainingSeconds = remainingSeconds - (hours * 60 * 60);
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        String currentTimer = getString(R.string.txt_timer_template).replace("$HOURS", df.format(hours))
                .replace("$MINUTES", df.format(minutes))
                .replace("$SECONDS", df.format(seconds));
        txtTimer.setText(currentTimer);
    }

    /**
     * Resets the Buttons and the timer-TextView while also allowing to send a Toast to the user
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
        runOnUiThread(() -> {
            updateTimerValue(remainingTimeInSeconds);
        });
    }

    @Override
    public void onTimerFinished() {
        Log.d("TIMER_KEY", "Timer finished");
        runOnUiThread(() -> {
            resetAndNotify("Timer finished");
        });
    }


    @Override
    public void onTimerCancelled() {
        runOnUiThread(() -> {
            resetAndNotify("Timer cancelled");
        });
    }
}
