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

    public AudioCapture(int sampleRate, int bufferSize) {
        this.sampleRate = sampleRate;
        this.bufferSize = bufferSize;
    }

    public short[] read() {
        short[] buffer = new short[bufferSize];
        if (audioRecord == null) {
            System.out.println("Trying to read audio but AudioCapture is null");
            return new short[0];
        }
        int res = audioRecord.read(buffer, 0, bufferSize);
        if (res < 0) {
            System.out.println("AudioCapture error. Code: " + String.valueOf(res));
            return new short[0];
        }

        return buffer;
    }

    public boolean start(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: PLEASE PLEASE HANDLE THIS SITUATION LATER
            return false;
        }
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize
        );
        audioRecord.startRecording();
        return true;
    }

    public void stop() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }
}
