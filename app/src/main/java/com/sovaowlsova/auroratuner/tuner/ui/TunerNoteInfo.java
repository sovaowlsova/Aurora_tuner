package com.sovaowlsova.auroratuner.tuner.ui;

import com.sovaowlsova.auroratuner.core.data.Note;

public record TunerNoteInfo(Note note, Boolean inTune, double frequency) { }
