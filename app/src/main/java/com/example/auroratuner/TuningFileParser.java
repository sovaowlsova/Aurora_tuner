package com.example.auroratuner;

import android.content.Context;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;


public class TuningFileParser {
    private final Context context;

    public TuningFileParser(Context context) {
        this.context = context;
    }

    public Tuning parseFile(String path) throws JacksonException, IOException {
        Tuning tuning;

        try (InputStream fileInputStream = context.getAssets().open(path)) {
            ObjectMapper mapper = new ObjectMapper();
            tuning = mapper.readValue(fileInputStream, Tuning.class);
        } catch (IOException e) {
            System.out.println("IOException while trying to read file " + path);
            System.out.println(e.getMessage());
            throw e;
        }

        return tuning;
    }
}
