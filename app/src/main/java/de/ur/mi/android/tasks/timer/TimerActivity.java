package de.ur.mi.android.tasks.timer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_timer);
        // TODO: Intitalisieren Sie hier das UI, vor allem die notwendigen Listener für die Interaktion der User mit dem UI
    }
}
