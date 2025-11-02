package com.example.auroratuner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.Locale;

public class TunerFragment extends Fragment {

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
    }

    private void setFactorialText() {
        TextView text = getView().findViewById(R.id.factorial_text);
        long factorial = CombinatoricsUtils.factorial(5);
        text.setText(String.format(Locale.US, "factorial of 5 is: %d", factorial));
    }
}