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

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TunerFragment extends Fragment {

    TextView noteText;

    ScheduledExecutorService tunerScheduler;
    AudioRecord record;

    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private final int SAMPLE_RATE = 44100;
    private final int BUFFER_SIZE = 8192;

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
        setContextualVariables();
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
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, PermissionID.REQUEST_RECORD_AUDIO_PERMISSION.get());
            return;
        }

        System.out.println("Starting tuner");
        record = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        record.startRecording();

        tunerScheduler = Executors.newScheduledThreadPool(1);
        tunerScheduler.scheduleAtFixedRate(() -> {
            short[] buffer = new short[BUFFER_SIZE];
            int res = record.read(buffer, 0, BUFFER_SIZE);
            if (res < BUFFER_SIZE) {
                System.out.printf(Locale.US, "WARNING: couldn't read audio. res = %d%n", res);
            }
            double frequency = SoundProcessor.findFrequencyByAutocorrelation(buffer, SAMPLE_RATE);
            if (frequency < 0) {
                return;
            }
            requireActivity().runOnUiThread(() -> {
                noteText.setText(String.format(Locale.US, "%.2f Hz", frequency));
            });
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void stopTuner() {
        System.out.println("Stopping tuner");
        if (tunerScheduler != null) {
            tunerScheduler.shutdownNow();
            tunerScheduler = null;
        }
        if (record != null) {
            record.stop();
            record.release();
            record = null;
        }
    }

    private void setContextualVariables() {
        View view = getView();
        noteText = view.findViewById(R.id.note_text);
    }
}