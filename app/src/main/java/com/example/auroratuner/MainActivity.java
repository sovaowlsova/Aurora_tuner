package com.example.auroratuner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.loader.app.LoaderManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
        configureNavMenu();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Destroyed");
    }

    private void configureNavMenu() {
        final BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationPanel);
        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainView);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNav, navController);

        /*final FragmentContainerView mainView = findViewById(R.id.mainView);
        final BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationPanel);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_settings) {
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
        bottomNav.setSelectedItemId(activeItem);*/
    }
}