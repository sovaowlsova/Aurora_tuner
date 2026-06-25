package com.sovaowlsova.auroratuner.tuner.data;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;


public class SoundProcessor {
    private static final double ABSOLUTE_THRESHOLD = 0.1;

    // Here we implement YIN ourselves because TarsosDSP uses GPL-3.0 license
    /**
     * Finds fundamental pitch using the YIN algorithm
     *
     * @param samples    input 16-bit audio signal
     * @param sampleRate sample rate in Hz
     *
     * @return fundamental pitch or -1 if the pitch can not be found or is too weak
     *
     * @example
     * <pre>{@code
     * short[] audioBuffer = // get data
     * double pitch = SoundProcessor.findPitch(audioBuffer, 2048, 44100);
     * if (pitch > 0) {
     *     System.out.printf("Fundamental pitch: %.2f Hz%n", pitch);
     * } else {
     *     System.out.println("Couldn't find pitch");
     * }
     * }</pre>
     */
    public static double findPitch(short[] samples, int sampleRate) {
        double[] normalized = normalizeSignal(samples);
        applyHammingWindowDirectly(normalized);

        double[] differenceFunction = computeDifferenceFunction(normalized);
        double[] cumulativeNormalization = applyCumulativeNormalization(differenceFunction);

        int fundamentalPeriod = applyAbsoluteThreshold(cumulativeNormalization);
        double interpolatedPeriod = applyParabolicInterpolation(cumulativeNormalization, fundamentalPeriod);

        return sampleRate / interpolatedPeriod;
    }

    private static double[] computeDifferenceFunction(double[] normalized) {
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] fftResult = fft.transform(normalized, TransformType.FORWARD);

        double[] powerSpectrum = computePowerSpectrum(fftResult);

        Complex[] autocorrelation = fft.transform(powerSpectrum, TransformType.INVERSE);

        double[] differenceFunction = new double[normalized.length / 2];
        double energy = autocorrelation[0].getReal();

        for (int t = 0; t < differenceFunction.length; t++) {
            differenceFunction[t] = energy - autocorrelation[t].getReal();
        }

        return differenceFunction;
    }

    private static double[] computePowerSpectrum(Complex[] fftResult) {
        double[] powerSpectrum = new double[fftResult.length];

        for (int i = 0; i < powerSpectrum.length; i++) {
            double abs = fftResult[i].abs();
            powerSpectrum[i] = abs * abs;
        }

        return powerSpectrum;
    }

    private static double[] applyCumulativeNormalization(double[] differenceFunction) {
        double[] cumulative = new double[differenceFunction.length];
        cumulative[0] = 1;

        double sum = 0;
        for (int t = 1; t < differenceFunction.length; t++) {
            sum += differenceFunction[t];
            cumulative[t] = differenceFunction[t] / (sum / t);
        }

        return cumulative;
    }

    private static int applyAbsoluteThreshold(double[] cumulative) {
        for (int t = 2; t < cumulative.length; t++) {
            if (cumulative[t] < ABSOLUTE_THRESHOLD) {
                // Finding local minimum
                while (t + 1 < cumulative.length && cumulative[t + 1] < cumulative[t]) {
                    t++;
                }
                return t;
            }
        }
        // No trough found
        return -1;
    }

    private static double applyParabolicInterpolation(double[] cumulative, int t) {
        if (t <= 0 || t >= cumulative.length - 1) {
            return t;
        }

        double y1 = cumulative[t - 1];
        double y2 = cumulative[t];
        double y3 = cumulative[t + 1];

        return t + (y1 - y3) / (2 * (y1 - 2 * y2 + y3));
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
