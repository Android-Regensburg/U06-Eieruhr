package de.ur.mi.android.tasks.timer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import de.ur.mi.android.tasks.timer.broadcast.TimerBroadcastListener;
import de.ur.mi.android.tasks.timer.broadcast.TimerBroadcastReceiver;
import de.ur.mi.android.tasks.timer.timer.TimerService;
import de.ur.mi.android.tasks.timer.ui.TimeSelectorView;
import de.ur.mi.android.tasks.timer.ui.TimerView;

public class TimerActivity extends AppCompatActivity implements TimeSelectorView.TimeSelectorViewListener, TimerView.TimerViewListener, TimerBroadcastListener {

    private TimeSelectorView selectorView;
    private TimerView timerView;
    private TimerService timerService;
    private TimerBroadcastReceiver timerReceiver;
    private boolean timerServiceBounded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_timer);
        selectorView = new TimeSelectorView(findViewById(R.id.time_select_container), this);
        timerView = new TimerView(findViewById(R.id.timer_container), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService();
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopService();
    }


    private void startService() {
        timerReceiver = new TimerBroadcastReceiver((this));
        this.registerReceiver(timerReceiver, TimerBroadcastReceiver.getIntentFilter());
        Intent intent = new Intent(this, TimerService.class);
        startForegroundService(intent);
        bindService(intent, timerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopService() {
        if (timerReceiver != null) {
            unregisterReceiver(timerReceiver);
        }
        unbindService(timerServiceConnection);
        timerServiceBounded = false;
    }

    @Override
    public void onTimeSelected(int seconds) {
        timerView.setTime(seconds);
    }

    @Override
    public void onTimeAdded(int seconds) {
        timerView.addTime(seconds);
    }

    @Override
    public void onTimeRemoved(int seconds) {
        timerView.removeTime(seconds);
    }

    @Override
    public void onTimerStartButtonClicked() {
        Log.d("QuickTimer", "in: onTimerStartButtonClicked");
        timerService.start(timerView.getTime());
    }

    @Override
    public void onTimerPausedButtonClicked() {
        Log.d("QuickTimer", "in: onTimerPausedButtonClicked");
        timerService.pause();
    }

    @Override
    public void onTimerResumedButtonClicked() {
        Log.d("QuickTimer", "in: onTimerResumedButtonClicked");
        timerService.resume();
    }

    @Override
    public void onTimerResetButtonClicked() {
        Log.d("QuickTimer", "in: onTimerResetButtonClicked");
        timerService.stop();
    }

    @Override
    public void onTimerStarted() {
        Log.d("QuickTimer", "in: onTimerStarted");
    }

    @Override
    public void onTimerResumed() {
        Log.d("QuickTimer", "in: onTimerResumed");
    }

    @Override
    public void onTimerPaused() {
        Log.d("QuickTimer", "in: onTimerPaused");
    }

    @Override
    public void onTimerStopped() {
        Log.d("QuickTimer", "in: onTimerStopped");
        timerView.reset();
    }

    @Override
    public void onTimerUpdated(int remainingTimeInSeconds) {
        Log.d("QuickTimer", "in: onTimerUpdated (" + remainingTimeInSeconds + ")");
        runOnUiThread(() -> timerView.updateTime(remainingTimeInSeconds));
    }

    private final ServiceConnection timerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            TimerService.TimerServiceBinder binder = (TimerService.TimerServiceBinder) service;
            timerService = binder.getService();
            timerServiceBounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            timerServiceBounded = false;
        }
    };

}
