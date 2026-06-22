package com.example.auroratuner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TunerFragment extends Fragment {

    TextView noteText;
    TextView deltaText;
    AutoCompleteTextView instrumentSelectionDropdown;
    AutoCompleteTextView tuningSelectionSpinnerDropdown;
    ConstraintLayout tunerSectionConstraintLayout;
    ConstraintSet tunerSectionConstraintSet;
    ScheduledExecutorService tunerScheduler;
    AudioRecord record;

    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private String[] currentTunings;
    private static final HashMap<String, Class<? extends Fragment>> commonInstruments;

    private final int SAMPLE_RATE = 44100;
    private final int BUFFER_SIZE = 8192;
    private final float HIT_PRECISION = 0.5f;
    private final float MAX_LINE_BIAS = 0.895f;
    private final float MIN_LINE_BIAS = 0.105f;
    private final float MAX_NOTE_DELTA = 0.03f;

    private final float BIAS_INTERVAL = MAX_LINE_BIAS - 0.5f;

    static {
        commonInstruments = new HashMap<>();
        commonInstruments.put("guitar", GuitarFragment.class);
        commonInstruments.put("ukulele", UkuleleFragment.class);
    }

    public TunerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            startTuner();
        } else {
            stopTuner();
            noteText.setText(R.string.note_text_placeholder);
            noteText.setTextColor(Color.parseColor("#FFFFFF"));
            tunerSectionConstraintSet.setHorizontalBias(R.id.measure_line, 0.5f);
            deltaText.setText("");
        }
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
        view.post(() -> {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.instrument_view, createNewInstance(GuitarFragment.class))
                    .commit();
        });
        startTuner();
        setInstrumentSelectionSpinnerVariables();
        setTuningSelectionSpinnerVariables();
//        TuningFileParser testParser = new TuningFileParser(getContext());
//        try {
//            Tuning testTuning = testParser.parseFile("tunings/tuning_guitar6_openE.json");
//            System.out.println("Got a tuning!");
//            System.out.println("Name: " + testTuning.getTuningName());
//            System.out.println("Instrument: " + testTuning.getInstrumentId());
//            System.out.println("Tuning: " + testTuning.getTuning().toString());
//        } catch (Exception ignored) {}
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
        tunerScheduler.scheduleWithFixedDelay(() -> {
            short[] buffer = new short[BUFFER_SIZE];
            int res = record.read(buffer, 0, BUFFER_SIZE);
            if (res < BUFFER_SIZE) {
                System.out.printf(Locale.US, "WARNING: couldn't read audio. res = %d%n", res);
            }
            double frequency = SoundProcessor.findPitch(buffer, SAMPLE_RATE);
            if (frequency < 0) {
                return;
            }
            Note baseNote = Note.frequencyToNote(frequency);
            if (baseNote == null) {
                return;
            }
            requireActivity().runOnUiThread(() -> {
                float percentsDelta = baseNote.getPercentsDelta(frequency);
                float invertedPercentsDelta = percentsDelta * -1;
                float mappedPercentsDelta = invertedPercentsDelta / MAX_NOTE_DELTA;
                float lineBias = 0.5f + BIAS_INTERVAL * mappedPercentsDelta;
                float clampedLineBias = Math.max(MIN_LINE_BIAS, Math.min(MAX_LINE_BIAS, lineBias));

                tunerSectionConstraintSet.setHorizontalBias(R.id.measure_line, clampedLineBias);

                noteText.setText(String.format(Locale.US, "%s(%.2f)", baseNote.getName(), baseNote.getFrequency()));
                deltaText.setText(baseNote.getSignedDelta(frequency, 2));
                if (Math.abs(baseNote.getDelta(frequency)) < HIT_PRECISION) {
                    noteText.setTextColor(Color.parseColor("#00FF00"));
                } else {
                    noteText.setTextColor(Color.parseColor("#FFFFFF"));
                }
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
        deltaText = view.findViewById(R.id.delta_text);
        instrumentSelectionDropdown = view.findViewById(R.id.instrument_selection_dropdown);
        tuningSelectionSpinnerDropdown = view.findViewById(R.id.tuning_selection_dropdown);

        tunerSectionConstraintLayout = view.findViewById(R.id.tuner_section_layout);
        tunerSectionConstraintSet = new ConstraintSet();
        tunerSectionConstraintSet.clone(tunerSectionConstraintLayout);
        tunerSectionConstraintLayout.setConstraintSet(tunerSectionConstraintSet);
    }

    private void setInstrumentSelectionSpinnerVariables() {
        String[] instrument_keys = getResources().getStringArray(R.array.instrument_array_keys);

        Context context = getContext();
        if (context == null) {
            System.out.println("WARNING: Couldn't find a context");
            return;
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.instruments_array,
                R.layout.dropdown_item
        );

        adapter.setDropDownViewResource(R.layout.dropdown_item);
        instrumentSelectionDropdown.setAdapter(adapter);

        instrumentSelectionDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String instrument = position < instrument_keys.length ? instrument_keys[position] : (String)parent.getItemAtPosition(position);
            switchInstrumentView(instrument);
        });
    }

    private void setTuningSelectionSpinnerVariables() {
        Context context = getContext();
        if (context == null) {
            System.out.println("WARNING: Couldn't find a context");
            return;
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.guitar_tunings,
                R.layout.dropdown_item
        );

        adapter.setDropDownViewResource(R.layout.dropdown_item);
        tuningSelectionSpinnerDropdown.setAdapter(adapter);
    }

    private void switchInstrumentView(String instrumentName) {
        if (commonInstruments.containsKey(instrumentName)) {
            System.out.println("Common instrument selected");
            System.out.println(instrumentName);

            Fragment newFragment = createNewInstance(commonInstruments.get(instrumentName));
            if (newFragment == null) {
                return;
            }

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.instrument_view, newFragment)
                    .commit();
        }
    }

    private List<Tuning> getInstrumentTunings(String instrumentId) {
        List<Tuning> tunings = new ArrayList<>();
        if (Arrays.stream(BuiltInInstruments.values()).anyMatch(instrument -> instrument.getIdName().equals(instrumentId))) {
            tunings.addAll(Arrays.asList(BuiltInInstruments.valueOf(instrumentId).getBuiltInTunings()));
        }
        return tunings;
    }

    private Fragment createNewInstance(Class<? extends Fragment> fragmentClass) {
        try {
            assert fragmentClass != null;
            Constructor<? extends Fragment> constructor = fragmentClass.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            System.out.println("Error! Couldn't create a fragment");
            return null;
        }
    }
}