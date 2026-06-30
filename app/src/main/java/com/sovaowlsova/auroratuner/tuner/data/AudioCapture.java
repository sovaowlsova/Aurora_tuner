package com.sovaowlsova.auroratuner.tuner.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;

public class AudioCapture {
    private final int sampleRate;
    private final int bufferSize;
    private AudioRecord audioRecord;
    private volatile boolean recording = false;

    public AudioCapture(int sampleRate, int bufferSize) {
        this.sampleRate = sampleRate;
        this.bufferSize = bufferSize;
    }

    public short[] read() {
        short[] buffer = new short[bufferSize];
        if (audioRecord == null || !recording) {
            System.out.println("Trying to read audio but AudioCapture is stopped");
            return new short[0];
        }
        try {
            int res = audioRecord.read(buffer, 0, bufferSize);
            if (res < 0) {
                System.out.println("AudioCapture error. Code: " + String.valueOf(res));
                return new short[0];
            }
            return buffer;
        } catch (Exception e) {
           System.out.println("AudioCapture error: " + e.getMessage());
           return new short[0];
        }
    }

    public boolean start(Context context) {
        System.out.println("AudioCapture: starting");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Audio permission isn't granted. Can not start the tuner");
            return false;
        }
        if (audioRecord != null) {
            System.out.println("Recording is already started");
            return true;
        }
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize
        );
        recording = true;
        audioRecord.startRecording();
        return true;
    }

    public void stop() {
        recording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        } else {
            System.out.println("Trying to stop already stopped AudioCapture");
        }
    }
}
