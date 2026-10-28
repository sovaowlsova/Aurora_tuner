package com.sovaowlsova.auroratuner.editor.model;

public record ExportTuningPayload(
        String instrumentId,
        String tuningId,
        String authorNickname,
        LocalizedNamesRVAdapter localizedInstrumentNames,
        LocalizedNamesRVAdapter localizedTuningNames,
        NotesRVAdapter notes
) {}
