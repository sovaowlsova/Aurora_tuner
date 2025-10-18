package com.example.auroratuner;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class PermissionFragment extends Fragment {

    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};

    public PermissionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_permission, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureAllowButton();
    }

    private void configureAllowButton() {
        Button allowButton = getView().findViewById(R.id.allow_button);
        allowButton.setOnClickListener(v ->
                ActivityCompat.requestPermissions(requireActivity(), permissions,
                PermissionID.REQUEST_RECORD_AUDIO_PERMISSION.get()));
    }

}