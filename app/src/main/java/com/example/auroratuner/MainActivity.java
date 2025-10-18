package com.example.auroratuner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.loader.app.LoaderManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity {
    String audioPermission = Manifest.permission.RECORD_AUDIO;
    private BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomNavigationPanel);
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
        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainView);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.tunerFragment) {
                int checkVal = getBaseContext().checkSelfPermission(audioPermission);
                if (checkVal != PackageManager.PERMISSION_GRANTED &&
                        navController.getCurrentDestination().getId() != R.id.permissionFragment) {
                    navController.navigate(R.id.permissionFragment);
                    return true;
                } else {
                    navController.navigate(R.id.tunerFragment);
                    return true;
                }
            } else if (id == navController.getCurrentDestination().getId()) {
                    return false;
            } else {
                navController.navigate(id);
                return true;
            }
        });

        bottomNav.setSelectedItemId(R.id.tunerFragment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionID.REQUEST_RECORD_AUDIO_PERMISSION.get() &&
        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            bottomNav.setSelectedItemId(R.id.tunerFragment);
        }
    }
}