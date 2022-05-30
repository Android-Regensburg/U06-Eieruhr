package de.ur.mi.android.tasks.timer.ui;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import de.ur.mi.android.tasks.timer.R;
import de.ur.mi.android.tasks.timer.timer.TimerState;

public class TimerView {

    private static final int DEFAULT_TIME_IN_SECONDS = 60;
    private static final int MIN_TIME_IN_SECONDS = 0;
    private static final int MAX_TIME_IN_SECONDS = (99 * 60) + 59;

    private final TextView[] digits;
    private final Button timerControlButton;
    private final Button timerResetButton;
    private final TimerViewListener listener;
    private int currentTimeInSeconds;
    private TimerState currentState;

    public TimerView(ConstraintLayout parent, TimerViewListener listener) {
        this.listener = listener;
        digits = new TextView[4];
        digits[0] = parent.findViewById(R.id.timer_minutes_decimal);
        digits[1] = parent.findViewById(R.id.timer_minutes);
        digits[2] = parent.findViewById(R.id.timer_seconds_decimal);
        digits[3] = parent.findViewById(R.id.timer_seconds);
        timerControlButton = parent.findViewById(R.id.timer_control_button);
        timerResetButton = parent.findViewById(R.id.timer_reset_button);
        timerControlButton.setOnClickListener(v -> handleControlButtonClick());
        timerResetButton.setOnClickListener(v -> handleResetButtonClick());
        currentTimeInSeconds = 0;
        currentState = TimerState.IDLE;
        updateTime(DEFAULT_TIME_IN_SECONDS);
    }

    private void handleControlButtonClick() {
        switch (currentState) {
            case IDLE:
                currentState = TimerState.RUNNING;
                listener.onTimerStartButtonClicked();
                break;
            case PAUSED:
                currentState = TimerState.RUNNING;
                listener.onTimerResumedButtonClicked();
                break;
            case RUNNING:
                currentState = TimerState.PAUSED;
                listener.onTimerPausedButtonClicked();
                break;
            default:
                break;
        }
        updateUI();
    }

    private void handleResetButtonClick() {
        currentState = TimerState.IDLE;
        listener.onTimerResetButtonClicked();
        updateUI();
    }


    public void updateTime(int seconds) {
        if (seconds >= MIN_TIME_IN_SECONDS && seconds <= MAX_TIME_IN_SECONDS) {
            currentTimeInSeconds = seconds;
            updateUI();
        }
    }

    public void setTime(int seconds) {
        if (currentState == TimerState.IDLE) {
            updateTime(seconds);
            updateUI();
        }
    }


    public void addTime(int seconds) {
        if (currentState == TimerState.IDLE) {
            updateTime(currentTimeInSeconds + seconds);
            updateUI();
        }
    }

    public void removeTime(int seconds) {
        if (currentState == TimerState.IDLE) {
            updateTime(currentTimeInSeconds - seconds);
            updateUI();
        }
    }

    public void reset() {
        currentState = TimerState.IDLE;
        updateTime(DEFAULT_TIME_IN_SECONDS);
    }

    private void updateUI() {
        int minutesToShow = currentTimeInSeconds / 60;
        int secondsToShow = currentTimeInSeconds % 60;
        digits[0].setText(String.valueOf(minutesToShow / 10));
        digits[1].setText(String.valueOf(minutesToShow % 10));
        digits[2].setText(String.valueOf(secondsToShow / 10));
        digits[3].setText(String.valueOf(secondsToShow % 10));
        switch (currentState) {
            case IDLE:
            case PAUSED:
                timerControlButton.setText(R.string.timer_start_button_label);
                timerControlButton.setEnabled(true);
                timerResetButton.setEnabled(true);
                break;
            case RUNNING:
                timerControlButton.setText(R.string.timer_pause_button_label);
                timerControlButton.setEnabled(true);
                timerResetButton.setEnabled(false);
                break;
        }
    }

    public int getTime() {
        return currentTimeInSeconds;
    }

    public interface TimerViewListener {
        void onTimerStartButtonClicked();

        void onTimerPausedButtonClicked();

        void onTimerResumedButtonClicked();

        void onTimerResetButtonClicked();
    }


}
