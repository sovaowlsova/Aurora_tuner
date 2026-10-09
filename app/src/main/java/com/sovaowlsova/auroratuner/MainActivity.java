package com.sovaowlsova.auroratuner;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sovaowlsova.auroratuner.core.di.AppContainer;
import com.sovaowlsova.auroratuner.core.model.FragmentTag;
import com.sovaowlsova.auroratuner.core.util.FragmentFactory;
import com.sovaowlsova.auroratuner.permission.data.PermissionID;
import com.sovaowlsova.auroratuner.permission.ui.PermissionFragment;
import com.sovaowlsova.auroratuner.editor.ui.EditorFragment;
import com.sovaowlsova.auroratuner.news.ui.NewsFragment;
import com.sovaowlsova.auroratuner.settings.ui.SettingsFragment;
import com.sovaowlsova.auroratuner.tuner.ui.TunerFragment;

import androidx.annotation.NonNull;

import java.util.Map;


public class MainActivity extends AppCompatActivity {
    String audioPermission = Manifest.permission.RECORD_AUDIO;
    private static final int MAIN_VIEW_ID = R.id.mainView;
    private Fragment currentFragment;
    private int lastBottomNavItemId;
    private Fragment tunerFragment;
    private Fragment newsFragment;
    private Fragment editorFragment;
    private Fragment permissionFragment;
    private Fragment settingsFragment;
    private BottomNavigationView bottomNav;
    private MaterialToolbar toolbar;

    private final Map<FragmentTag, Integer> fragmentTagToId = Map.of(
        FragmentTag.NEWS, R.id.newsFragment,
        FragmentTag.EDITOR, R.id.editorFragment,
        FragmentTag.TUNER, R.id.tunerFragment
    );

    private static final Map<Integer, FragmentTag> idToFragmentTag = Map.of(
            R.id.newsFragment, FragmentTag.NEWS,
            R.id.editorFragment, FragmentTag.EDITOR,
            R.id.tunerFragment, FragmentTag.TUNER
    );
    private static final String KEY_SELECTED_TAB = "selected_tab";
    private static final String KEY_SETTINGS_OPEN = "settings_opened";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Created app");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNavigationPanel);
        toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackButton();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
            return;
        }

        bottomNav.setOnItemSelectedListener(this::bottomNavListener);
        bottomNav.setSelectedItemId(R.id.tunerFragment);
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Stopped");
        // Just to be safe
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
        outState.putBoolean(KEY_SETTINGS_OPEN, currentFragment == settingsFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("Inflating toolbar");
        getMenuInflater().inflate(R.menu.toolbar_default, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        System.out.println("Changing toolbar");
        menu.findItem(R.id.toolbar_settings).setVisible(settingsFragment == null || currentFragment != settingsFragment);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_settings) {
            System.out.println("Settings!");
            bottomNav.setVisibility(BottomNavigationView.GONE);
            lastBottomNavItemId = bottomNav.getSelectedItemId();
            settingsFragment = getOrCreateFragment(settingsFragment, SettingsFragment.class, FragmentTag.SETTINGS);
            switchMainView(settingsFragment, true, true);
            invalidateOptionsMenu();
        } else if (id == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void restoreState(Bundle savedInstanceState) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        tunerFragment = fragmentManager.findFragmentByTag(FragmentTag.TUNER.get());
        newsFragment = fragmentManager.findFragmentByTag(FragmentTag.NEWS.get());
        editorFragment = fragmentManager.findFragmentByTag(FragmentTag.EDITOR.get());
        permissionFragment = fragmentManager.findFragmentByTag(FragmentTag.PERMISSION.get());
        settingsFragment = fragmentManager.findFragmentByTag(FragmentTag.SETTINGS.get());

        bottomNav.setOnItemSelectedListener(this::bottomNavListener);
        int selectedTabId = savedInstanceState.getInt(KEY_SELECTED_TAB, R.id.tunerFragment);
        boolean settingsOpen = savedInstanceState.getBoolean(KEY_SETTINGS_OPEN, false);

        if (settingsOpen) {
            lastBottomNavItemId = selectedTabId;
            bottomNav.setVisibility(BottomNavigationView.GONE);
            currentFragment = settingsFragment;
        } else {
            bottomNav.setSelectedItemId(selectedTabId);
        }
    }

    private void switchMainView(Fragment targetFragment, boolean showTitle, boolean showBackButton) {
        System.out.println("Switching main view...");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.setReorderingAllowed(true);
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        transaction.runOnCommit(() -> {
                getSupportActionBar().setDisplayShowTitleEnabled(showTitle);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayHomeAsUpEnabled(showBackButton);
                }
            }
        );
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

    private Fragment getOrCreateFragment(Fragment fragment, Class<? extends Fragment> fragmentClass, FragmentTag tag) {
        if (fragment == null) {
            fragment = getSupportFragmentManager().findFragmentByTag(tag.get());
        }
        if (fragment == null) {
            fragment = FragmentFactory.create(fragmentClass);
        }
        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(MAIN_VIEW_ID, fragment, tag.get())
                    .commitNow();
        }
        return fragment;
    }

    private void handleBackButton() {
        System.out.println("Back button pressed");
        if (currentFragment == settingsFragment) {
            bottomNav.setSelectedItemId(lastBottomNavItemId);
            bottomNav.setVisibility(BottomNavigationView.VISIBLE);
            invalidateOptionsMenu();
        } else {
            finish();
        }
    }

    private boolean bottomNavListener(@NonNull MenuItem item) {
        int id = item.getItemId();
        System.out.println("id: " + id);
        FragmentTag fragmentTag;
        Fragment targetFragment;

        if (id == R.id.tunerFragment) {
            int checkVal = getBaseContext().checkSelfPermission(audioPermission);
            if (checkVal != PackageManager.PERMISSION_GRANTED) {
                System.out.println("Switching to permission...");
                fragmentTag = FragmentTag.PERMISSION;
                permissionFragment = getOrCreateFragment(permissionFragment, PermissionFragment.class, fragmentTag);
                if (tunerFragment != null && tunerFragment.isAdded()) {
                    getSupportFragmentManager().beginTransaction()
                            .hide(tunerFragment)
                            .commitNow();
                }
                targetFragment = permissionFragment;
            } else {
                System.out.println("Switching to tuner...");
                fragmentTag = FragmentTag.TUNER;
                tunerFragment = getOrCreateFragment(tunerFragment, TunerFragment.class, fragmentTag);
                targetFragment = tunerFragment;
            }
        } else if (id == R.id.newsFragment) {
            System.out.println("Switching to news...");
            fragmentTag = FragmentTag.NEWS;
            newsFragment = getOrCreateFragment(newsFragment, NewsFragment.class, fragmentTag);
            targetFragment = newsFragment;
        } else if (id == R.id.editorFragment) {
            System.out.println("Switching to editor...");
            fragmentTag = FragmentTag.EDITOR;
            editorFragment = getOrCreateFragment(editorFragment, EditorFragment.class, fragmentTag);
            targetFragment = editorFragment;
        } else {
            System.out.println("Unknown fragment");
            return false;
        }

        System.out.println("target is null: " + ((targetFragment == null)));
        if (targetFragment != null && targetFragment != currentFragment) {
            switchMainView(targetFragment,
                    targetFragment != tunerFragment,
                    targetFragment == settingsFragment);
            return true;
        } else {
            System.out.println("Cancelling the switch: already there");
        }
        return false;
    }
}