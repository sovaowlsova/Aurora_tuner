package com.example.auroratuner;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class SoundProcessor {
    /*public static double findDominatingFrequency(short[] audioData, int bufferSize, int sampleRate) {
        Complex[] complexSamples = new Complex[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            complexSamples[i] = new Complex(audioData[i], 0);
        }

        FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] fftResult = fastFourierTransformer.transform(complexSamples, TransformType.FORWARD);

        return findFrequencyByAutocorrelation(fftResult, sampleRate);
    }*/

    public static double findFrequencyByAutocorrelation(short[] samples, int sampleRate) {
        int minFrequency = 80;
        int maxFrequncy = 2000;

        int minLag = sampleRate / maxFrequncy;
        int maxLag = sampleRate / minFrequency;

        double[] normalized = normalizeSignal(samples);

        double maxCorrelation = -1;
        int bestLag = minFrequency;

        for (int lag = minLag; lag <= maxLag; lag += 1) {
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

        double frequency = (double) sampleRate / bestLag;

        if (frequency < minFrequency || frequency > maxFrequncy) {
            return -1;
        }

        return frequency;
    }

    private static double[] normalizeSignal(short[] samples) {
        double[] normalized = new double[samples.length];

        double max = 0;
        for (short sample : samples) {
            if (Math.abs(sample) > max) {
                max = Math.abs(sample);
            }
        }

        for (int i = 0; i < samples.length; i++) {
            normalized[i] = samples[i] / max;
        }

        return normalized;
    }
}
