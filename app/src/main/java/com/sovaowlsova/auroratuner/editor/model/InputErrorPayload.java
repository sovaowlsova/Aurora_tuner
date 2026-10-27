package com.sovaowlsova.auroratuner.editor.model;

public record InputErrorPayload(
        int instrumentIdErrorId,
        int tuningIdErrorId,
        int localizedInstrumentNamesErrorId,
        int localizedTuningNamesErrorId
) {}
