package com.example.auroratuner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Fuck");
        final BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationPanel);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_settings) {
                System.out.println("Shit");
                return true;
            } else if (id == R.id.bottom_tuner) {
                System.out.println("Dick");
                return true;
            } else if (id == R.id.bottom_news) {
                System.out.println("Amogus");
                return true;
            } else {
                System.out.println("WTF");
                return false;
            }
        });
        bottomNav.setSelectedItemId(R.id.bottom_tuner);
    }
}