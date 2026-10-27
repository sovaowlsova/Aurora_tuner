package com.sovaowlsova.auroratuner.core.model;

import java.util.HashMap;

public record JsonTuning(String instrumentId, String tuningId, String authorNickname,
                         HashMap<String, String> localizedInstrumentNames,
                         HashMap<String, String> localizedTuningNames) {
}
