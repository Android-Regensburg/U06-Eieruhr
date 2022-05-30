package de.ur.mi.android.tasks.timer.ui;

import android.widget.GridLayout;

import de.ur.mi.android.tasks.timer.R;

public class TimeSelectorView {

    public final TimeSelectorViewListener listener;

    public TimeSelectorView(GridLayout parent, TimeSelectorViewListener listener) {
        this.listener = listener;
        parent.findViewById(R.id.time_select_button_one_minute).setOnClickListener(v -> onTimeSelected(1));
        parent.findViewById(R.id.time_select_button_three_minutes).setOnClickListener(v -> onTimeSelected(3));
        parent.findViewById(R.id.time_select_button_five_minutes).setOnClickListener(v -> onTimeSelected(5));
        parent.findViewById(R.id.time_select_button_fifteen_minutes).setOnClickListener(v -> onTimeSelected(15));
        parent.findViewById(R.id.time_select_button_thirty_minutes).setOnClickListener(v -> onTimeSelected(30));
        parent.findViewById(R.id.time_select_button_sixty_minutes).setOnClickListener(v -> onTimeSelected(60));
        parent.findViewById(R.id.time_select_button_add_minute).setOnClickListener(v -> onTimeChanged(1));
        parent.findViewById(R.id.time_select_button_subtract_minute).setOnClickListener(v -> onTimeChanged(-1));
    }

    private void onTimeSelected(int minutes) {
        listener.onTimeSelected(minutes * 60);
    }

    private void onTimeChanged(int minutes) {
        if (minutes >= 0) {
            listener.onTimeAdded(minutes * 60);
        } else {
            listener.onTimeRemoved(-(minutes * 60));
        }
    }

    public interface TimeSelectorViewListener {

        void onTimeSelected(int seconds);

        void onTimeAdded(int seconds);

        void onTimeRemoved(int seconds);
    }
}
