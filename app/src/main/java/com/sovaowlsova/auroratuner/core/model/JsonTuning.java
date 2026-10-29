package com.sovaowlsova.auroratuner.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder({
        "instrumentId",
        "tuningId",
        "authorNickname",
        "localizedInstrumentNames",
        "localizedTuningNames",
        "notes"
})
public record JsonTuning(
        @JsonProperty("instrumentId") String instrumentId,
        @JsonProperty("tuningId") String tuningId,
        @JsonProperty("authorNickname") String authorNickname,
        @JsonProperty("localizedInstrumentNames") Map<String, String> localizedInstrumentNames,
        @JsonProperty("localizedTuningNames") Map<String, String> localizedTuningNames,
        @JsonProperty("notes") List<String> notes
) {
    @JsonCreator
    public JsonTuning {
    }
}