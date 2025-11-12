package com.example.auroratuner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.Locale;

public class TunerFragment extends Fragment {

    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private final int SAMPLE_RATE = 4410;
    private final int BUFFER_SIZE = 2048;

    public TunerFragment() {
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
        return inflater.inflate(R.layout.fragment_tuner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setFactorialText();
        startTuner();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTuner();
    }

    private void startTuner() {
        Context context = getContext();
        if (context == null) {
            System.out.println("WARNING: Couldn't find a context");
            return;
        }
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, PermissionID.REQUEST_RECORD_AUDIO_PERMISSION.get());
            return;
        }
        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        byte[] buffer = new byte[BUFFER_SIZE];
        int res = record.read(buffer, 0, BUFFER_SIZE);
        if (res < 0) {
            System.out.println("WARNING: couldn't read audio");
        }

    }

    private void stopTuner() {

    }

    private void setFactorialText() {
        TextView text = getView().findViewById(R.id.factorial_text);
        long factorial = CombinatoricsUtils.factorial(5);
        text.setText(String.format(Locale.US, "factorial of 5 is: %d", factorial));
    }
}