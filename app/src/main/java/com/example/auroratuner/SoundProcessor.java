package com.example.auroratuner;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.Arrays;
import java.util.Locale;

public class SoundProcessor {
    private static final double MIN_CORRELATION = 0.05;
    private static final double FIND_RANGE = 1.5;
    // private static final double WEIGHT = 0.7;
    public static double findDominatingFrequency(short[] audioData, int bufferSize, int sampleRate) {
        double[] normalized = normalizeSignal(audioData);
        applyHammingWindowDirectly(normalized);

        Complex[] complexSamples = new Complex[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            complexSamples[i] = new Complex(normalized[i], 0);
        }

        FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] fftResult = fastFourierTransformer.transform(complexSamples, TransformType.FORWARD);

        double maxMagnitude = 0;
        int maxIndex = 0;

        for (int i = 1; i < fftResult.length / 2; i++) {
            double magnitude = fftResult[i].abs();

            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
                maxIndex = i;
            }
        }

        double frequency = (double) maxIndex * sampleRate / fftResult.length;

        return findFrequencyByAutocorrelation(normalized, sampleRate, frequency);
    }

    private static double findFrequencyByAutocorrelation(double[] normalizedSamples, int sampleRate, double baseNote) {
        int minFrequency = (int)(baseNote / FIND_RANGE);
        int maxFrequency = (int)(baseNote * FIND_RANGE);

        int minLag = sampleRate / maxFrequency;
        int maxLag = Math.min(sampleRate / minFrequency, normalizedSamples.length / 2);

        double maxCorrelation = -1;
        int bestLag = -1;

        for (int lag = minLag; lag <= maxLag; lag++) {
            double correlation = 0;

            for (int i = 0; i < normalizedSamples.length - lag; i++) {
                correlation += normalizedSamples[i] * normalizedSamples[i + lag];
            }

            correlation /= (normalizedSamples.length - lag);

            if (correlation > maxCorrelation) {
                maxCorrelation = correlation;
                bestLag = lag;
            }
        }

        if (maxCorrelation < MIN_CORRELATION) {
            return -1;
        }

        double frequency = (double) sampleRate / bestLag;

        if (frequency < minFrequency || frequency > maxFrequency) {
            return -1;
        }

        return frequency;
    }

    /* public static double findFrequencyByAutocorrelation(short[] samples, int sampleRate) {
        int minFrequency = 80;
        int maxFrequency = 1500;

        int minLag = sampleRate / maxFrequency;
        int maxLag = Math.min(sampleRate / minFrequency, samples.length / 2);

        double[] normalized = normalizeSignal(samples);
        applyHammingWindowDirectly(normalized);

        double maxCorrelation = -1;
        int bestLag = -1;

        for (int lag = minLag; lag <= maxLag; lag++) {
            double correlation = 0;

            for (int i = 0; i < normalized.length - lag; i++) {
                correlation += normalized[i] * normalized[i + lag];
            }

            correlation /= (normalized.length - lag);

            if (correlation > maxCorrelation) {
                maxCorrelation = correlation;
                bestLag = lag;
            }
        }

        if (maxCorrelation < MIN_CORRELATION) {
            return -1;
        }

        double frequency = (double) sampleRate / bestLag;

        if (frequency < minFrequency || frequency > maxFrequency) {
            return -1;
        }

        return frequency;
    }*/

    private static double[] normalizeSignal(short[] samples) {
        double[] normalized = new double[samples.length];
        double max = 0;

        for (short sample : samples) {
            if (Math.abs(sample) > max) {
                max = Math.abs(sample);
            }
        }

        for (int i = 0; i < samples.length; i++) {
            normalized[i] = (double) samples[i] / max;
        }

        return normalized;
    }

    private static void applyHammingWindowDirectly(double[] normalizedSamples) {
        int length = normalizedSamples.length;
        for (int i = 0; i < length; i++) {
            normalizedSamples[i] *= 0.54 - 0.46 * Math.cos((2 * Math.PI * i) / (length - 1));
        }
    }
}
