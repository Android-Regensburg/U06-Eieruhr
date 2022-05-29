package de.ur.mi.android.tasks.timer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initUI() {
        setContentView(R.layout.activity_timer);
        // TODO: Intitalisieren Sie hier das UI, vor allem die notwendigen Listener für die Interaktion der User mit dem UI
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO: Reagieren Sie hier darauf, dass die Anwendung wieder in den Vordergrund verschoben wurde
    }

    @Override
    protected void onStop() {
        super.onStop();
        // TODO: Reagieren Sie hier darauf, dass die Anwendung in den Hintergrund verschoben wurde
    }
}
