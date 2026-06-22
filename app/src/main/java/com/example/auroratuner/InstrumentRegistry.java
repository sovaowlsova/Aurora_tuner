package com.example.auroratuner;

import java.util.ArrayList;
import java.util.List;

public class InstrumentRegistry {
    private static InstrumentRegistry instance;
    private final List<Instrument> instruments = new ArrayList<>();

    private InstrumentRegistry() {
        registerBuiltIn();
    }

    public static InstrumentRegistry getInstance() {
        if (instance == null) {
            instance = new InstrumentRegistry();
        }
        return instance;
    }

    private void registerBuiltIn() {
        register(new Instrument("Guitar6", "Guitar", List.of(BuiltInTunings.Guitar6Standard.get(), BuiltInTunings.Guitar6DropD.get())));
        register(new Instrument("Ukulele", "Ukulele", List.of(BuiltInTunings.UkuleleStandard.get())));
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
        instruments.add(instrument);
    }
}
