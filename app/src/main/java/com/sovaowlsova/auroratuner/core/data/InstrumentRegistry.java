package com.sovaowlsova.auroratuner.core.data;

import com.sovaowlsova.auroratuner.R;
import com.sovaowlsova.auroratuner.core.model.BuiltInInstrument;
import com.sovaowlsova.auroratuner.core.model.BuiltInTunings;
import com.sovaowlsova.auroratuner.core.model.Instrument;
import com.sovaowlsova.auroratuner.core.model.Tuning;
import com.sovaowlsova.auroratuner.tuner.ui.instruments.GuitarFragment;
import com.sovaowlsova.auroratuner.tuner.ui.instruments.UkuleleFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class InstrumentRegistry {
    private static InstrumentRegistry instance;
    // A list because dropdowns in TunerFragment are dependent on the order
    private final List<Instrument> instruments;
    private final HashSet<String> uniqueIds;

    private InstrumentRegistry() {
        instruments = new ArrayList<>();
        uniqueIds = new HashSet<>();
        registerBuiltIn();
    }

    public static InstrumentRegistry getInstance() {
        if (instance == null) {
            instance = new InstrumentRegistry();
        }
        return instance;
    }

    private void registerBuiltIn() {
        register(new BuiltInInstrument(
                "Guitar6",
                R.string.instrument_guitar,
                6,
                List.of(BuiltInTunings.Guitar6Standard.get(),
                        BuiltInTunings.Guitar6DropD.get(),
                        BuiltInTunings.Guitar6OpenC.get()
                ),
                GuitarFragment.class
                )
        );
        register(new BuiltInInstrument(
                "Ukulele",
                R.string.instrument_ukulele,
                4,
                List.of(
                        BuiltInTunings.UkuleleStandard.get(),
                        BuiltInTunings.UkuleleLowG.get(),
                        BuiltInTunings.UkuleleLowA.get()
                        ),
                UkuleleFragment.class
                )
        );
        register(new TestGeneratedInstrument(
                "TestGenerated",
                "Test",
                10,
                List.of(
                        new Tuning("TestGenerated", "Chinese",
                                new Note[] {Note.C4, Note.C4sharp, Note.D4, Note.A3, Note.E4, Note.F4, Note.G3, Note.G2, Note.A3, Note.F4sharp})
                )
        ));
    }

    public List<Instrument> getAll() {
        return new ArrayList<>(instruments);
    }

    public Instrument getById(String id) {
        for (Instrument inst : instruments) {
            if (inst.getId().equals(id)) return inst;
        }
        return null;
    }

    public void register(Instrument instrument) {
        if (!uniqueIds.contains(instrument.getId())) {
            instruments.add(instrument);
            uniqueIds.add(instrument.getId());
        } else {
            List<Tuning> newTunings = instrument.getTunings();
            if (newTunings != null) {
                getById(instrument.getId()).addTunings(newTunings);
            }
        }
    }
}
