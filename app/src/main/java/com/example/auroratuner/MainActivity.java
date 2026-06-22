package com.example.auroratuner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
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

    private static final int MAIN_VIEW_ID = R.id.mainView;
    private Fragment currentFragment;
    private Fragment tunerFragment;
    private Fragment newsFragment;
    private Fragment editorFragment;
    private Fragment permissionFragment;
    private BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomNavigationPanel);

        // Creating fragments for further use
        tunerFragment = new TunerFragment();
        newsFragment = new NewsFragment();
        editorFragment = new EditorFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                        .add(MAIN_VIEW_ID, newsFragment)
                        .add(MAIN_VIEW_ID, editorFragment)
                        .hide(newsFragment)
                        .hide(editorFragment);

        int checkVal = getBaseContext().checkSelfPermission(audioPermission);
        if (checkVal != PackageManager.PERMISSION_GRANTED) {
            permissionFragment = new PermissionFragment();
            transaction.add(MAIN_VIEW_ID, permissionFragment);
        } else {
            transaction.add(MAIN_VIEW_ID, tunerFragment);
        }
        transaction.commitNow();

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
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment targetFragment = null;

            if (id == R.id.tunerFragment) {
                int checkVal = getBaseContext().checkSelfPermission(audioPermission);
                if (checkVal != PackageManager.PERMISSION_GRANTED) {
                    targetFragment = permissionFragment;
                } else {
                    targetFragment = tunerFragment;
                }
            } else if (id == R.id.newsFragment) {
                targetFragment = newsFragment;
            } else if (id == R.id.editorFragment) {
                targetFragment = editorFragment;
            }

            if (targetFragment != null && targetFragment != currentFragment) {
                switchMainView(targetFragment);
                return true;
            }
            return false;
        });

        bottomNav.setSelectedItemId(R.id.tunerFragment);
    }

    private void switchMainView(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.setReorderingAllowed(true);
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        transaction.show(targetFragment).commit();
        currentFragment = targetFragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionID.REQUEST_RECORD_AUDIO_PERMISSION.get() &&
        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (permissionFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(permissionFragment)
                        .add(MAIN_VIEW_ID, tunerFragment)
                        .commitNow();
                permissionFragment = null;
            }
            if (tunerFragment != null) {
                bottomNav.setSelectedItemId(R.id.tunerFragment);
            }
        }
    }
}