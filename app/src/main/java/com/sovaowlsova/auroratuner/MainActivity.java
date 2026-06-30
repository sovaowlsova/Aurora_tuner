package com.sovaowlsova.auroratuner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sovaowlsova.auroratuner.core.di.AppContainer;
import com.sovaowlsova.auroratuner.core.util.FragmentFactory;
import com.sovaowlsova.auroratuner.permission.data.PermissionID;
import com.sovaowlsova.auroratuner.permission.ui.PermissionFragment;
import com.sovaowlsova.auroratuner.editor.ui.EditorFragment;
import com.sovaowlsova.auroratuner.news.ui.NewsFragment;
import com.sovaowlsova.auroratuner.tuner.ui.TunerFragment;

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
    private static final String KEY_SELECTED_TAB = "selected_tab";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Created app");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomNavigationPanel);

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
            return;
        }

        // Creating fragments for further use
        tunerFragment = new TunerFragment();
        newsFragment = new NewsFragment();
        editorFragment = new EditorFragment();
        permissionFragment = new PermissionFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                        .add(MAIN_VIEW_ID, newsFragment, "news")
                        .add(MAIN_VIEW_ID, editorFragment, "editor")
                        .hide(newsFragment)
                        .hide(editorFragment);

        int checkVal = getBaseContext().checkSelfPermission(audioPermission);
        if (checkVal != PackageManager.PERMISSION_GRANTED) {
            transaction.add(MAIN_VIEW_ID, permissionFragment, "permission");
        } else {
            System.out.println("Adding tuner...");
            transaction.add(MAIN_VIEW_ID, tunerFragment, "tuner");
        }
        transaction.commitNow();

        configureNavMenu();
        bottomNav.setSelectedItemId(R.id.tunerFragment);
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Stopped");
        AppContainer.getInstance().getTunerEngine().stopTuner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Destroyed");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_TAB, bottomNav.getSelectedItemId());
    }

    private void restoreState(Bundle savedInstanceState) {
        tunerFragment = getSupportFragmentManager().findFragmentByTag("tuner");
        newsFragment = getSupportFragmentManager().findFragmentByTag("news");
        editorFragment = getSupportFragmentManager().findFragmentByTag("editor");
        permissionFragment = getSupportFragmentManager().findFragmentByTag("permission");

        configureNavMenu();
        int selectedTabId = savedInstanceState.getInt(KEY_SELECTED_TAB, R.id.tunerFragment);
        bottomNav.setSelectedItemId(selectedTabId);
    }

    private void configureNavMenu() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            System.out.println("id: " + id);
            Fragment targetFragment = null;

            if (id == R.id.tunerFragment) {
                int checkVal = getBaseContext().checkSelfPermission(audioPermission);
                if (checkVal != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Switching to permission...");
                    permissionFragment = getOrCreateFragment(permissionFragment, PermissionFragment.class, "permission");
                    if (tunerFragment != null && tunerFragment.isAdded()) {
                        getSupportFragmentManager().beginTransaction()
                                .hide(tunerFragment)
                                .commitNow();
                    }
                    targetFragment = permissionFragment;
                } else {
                    System.out.println("Switching to tuner...");
                    tunerFragment = getOrCreateFragment(tunerFragment, TunerFragment.class, "tuner");
                    targetFragment = tunerFragment;
                }
            } else if (id == R.id.newsFragment) {
                System.out.println("Switching to news...");
                newsFragment = getOrCreateFragment(newsFragment, NewsFragment.class, "news");
                targetFragment = newsFragment;
            } else if (id == R.id.editorFragment) {
                System.out.println("Switching to editor...");
                editorFragment = getOrCreateFragment(editorFragment, EditorFragment.class, "editor");
                targetFragment = editorFragment;
            }

            System.out.println("target is null: " + ((targetFragment == null)));
            if (targetFragment != null && targetFragment != currentFragment) {
                switchMainView(targetFragment);
                return true;
            } else {
                System.out.println("Cancelling the switch: already there");
            }
            return false;
        });
    }

    private void switchMainView(Fragment targetFragment) {
        System.out.println("Switching main view...");
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
            System.out.println("Showing tuner after audio permission granted");
            if (permissionFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(permissionFragment)
                        .commitNow();
                permissionFragment = null;
            }

            if (tunerFragment == null) {
                tunerFragment = new TunerFragment();
            }

            if (!tunerFragment.isAdded()) {
                System.out.println("Adding tuner after audio permission...");
                getSupportFragmentManager().beginTransaction()
                        .add(MAIN_VIEW_ID, tunerFragment, "tuner")
                        .commitNow();
            }
            bottomNav.setSelectedItemId(R.id.tunerFragment);
        }
    }

    private Fragment getOrCreateFragment(Fragment fragment, Class<? extends Fragment> fragmentClass, String tag) {
        if (fragment == null) {
            fragment = getSupportFragmentManager().findFragmentByTag(tag);
        }
        if (fragment == null) {
            fragment = FragmentFactory.create(fragmentClass);
        }
        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(MAIN_VIEW_ID, fragment, tag)
                    .commitNow();
        }
        return fragment;
    }
}