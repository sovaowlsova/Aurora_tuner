package com.example.auroratuner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText ticket = findViewById(R.id.ticket);
        final TextView showcase = findViewById(R.id.showcase);
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            String ticketText = ticket.getText().toString();
            if (!ticketText.equals("")) {
                showcase.setText(ticketText);
            }
        });
    }
}