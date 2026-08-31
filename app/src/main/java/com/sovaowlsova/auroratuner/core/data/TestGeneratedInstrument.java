package com.sovaowlsova.auroratuner.core.data;

import com.sovaowlsova.auroratuner.core.model.JsonInstrument;
import com.sovaowlsova.auroratuner.core.model.Tuning;

import java.util.List;

public class TestGeneratedInstrument extends JsonInstrument {
    public TestGeneratedInstrument(String id, String name, int stringCount) {
        super(id, name, stringCount);
    }

    public TestGeneratedInstrument(String id, String name, int stringCount, List<Tuning> tunings) {
        super(id, name, stringCount, tunings);
    }
}
