package com.example.auroratuner;

import java.util.Locale;

public enum Note {
    C0("C0", 16.35),
    C1("C1", 32.7),
    C2("C2", 65.41),
    C3("C3", 130.81),
    C4("C4", 261.63),
    C5("C5", 523.25),
    C6("C6", 1046.5),
    C7("C7", 2093),
    C8("C8", 4186),
    C0sharp("C0#", 17.32),
    C1sharp("C1#", 34.65),
    C2sharp("C2#", 69.3),
    C3sharp("C3#", 138.59),
    C4sharp("C4#", 277.18),
    C5sharp("C5#", 554.37),
    C6sharp("C6#", 1108.73),
    C7sharp("C7#", 2217.46),
    C8sharp("C8#", 4434.92),
    D0("D0", 18.35),
    D1("D1", 36.71),
    D2("D2", 73.42),
    D3("D3", 146.83),
    D4("D4", 293.66),
    D5("D5", 587.33),
    D6("D6", 1174.66),
    D7("D7", 2349.32),
    D8("D8", 4698.63),
    D0sharp("D0#", 19.45),
    D1sharp("D1#", 38.89),
    D2sharp("D2#", 77.78),
    D3sharp("D3#", 155.56),
    D4sharp("D4#", 311.13),
    D5sharp("D5#", 622.25),
    D6sharp("D6#", 1244.51),
    D7sharp("D7#", 2489),
    D8sharp("D8#", 4978),
    E0("E0", 20.6),
    E1("E1", 41.2),
    E2("E2", 82.41),
    E3("E3", 164.81),
    E4("E4", 329.63),
    E5("E5", 659.25),
    E6("E6", 1318.51),
    E7("E7", 2637),
    E8("E8", 5274),
    F0("F0", 21.83),
    F1("F1", 43.65),
    F2("F2", 87.31),
    F3("F3", 174.61),
    F4("F4", 349.23),
    F5("F5", 698.46),
    F6("F6", 1396.91),
    F7("F7", 2793.83),
    F8("F8", 5587.65),
    F0sharp("F0#", 23.12),
    F1sharp("F1#", 46.25),
    F2sharp("F2#", 92.5),
    F3sharp("F3#", 185),
    F4sharp("F4#", 369.99),
    F5sharp("F5#", 739.99),
    F6sharp("F6#", 1479.98),
    F7sharp("F7#", 2959.96),
    F8sharp("F8#", 5919.91),
    G0("G0", 24.5),
    G1("G1", 49),
    G2("G2", 98),
    G3("G3", 196),
    G4("G4", 392),
    G5("G5", 783.99),
    G6("G6", 1567.98),
    G7("G7", 3135.96),
    G8("G8", 6271.93),
    G0sharp("G0#", 25.96),
    G1sharp("G1#", 51.91),
    G2sharp("G2#", 103.83),
    G3sharp("G3#", 207.65),
    G4sharp("G4#", 415.3),
    G5sharp("G5#", 830.61),
    G6sharp("G6#", 1661.22),
    G7sharp("G7#", 3322.44),
    G8sharp("G8#", 6644.88),
    A0("A0", 27.5),
    A1("A1", 55),
    A2("A2", 110),
    A3("A3", 220),
    A4("A4", 440),
    A5("A5", 880),
    A6("A6", 1760),
    A7("A7", 3520),
    A8("A8", 7040),
    A0sharp("A0#", 29.14),
    A1sharp("A1#", 58.27),
    A2sharp("A2#", 116.54),
    A3sharp("A3#", 233.08),
    A4sharp("A4#", 466.16),
    A5sharp("A5#", 932.33),
    A6sharp("A6#", 1864.66),
    A7sharp("A7#", 3729.31),
    A8sharp("A8#", 7458.62),
    B0("B0", 30.87),
    B1("B1", 61.74),
    B2("B2", 123.47),
    B3("B3", 246.94),
    B4("B4", 493.88),
    B5("B5", 987.77),
    B6("B6", 1975.53),
    B7("B7", 3951),
    B8("B8", 7902.13);

    private final String name;
    private final double frequency;

    Note(String name, double frequency) {
        this.name = name;
        this.frequency = frequency;
    }

    public String getName() {
        return name;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getDelta(double otherFrequency) {
        return otherFrequency - frequency;
    }

    public String getSignedDelta(double otherFrequency, int precision) {
        double delta = getDelta(otherFrequency);
        return (delta > 0 ? "+" : "") +
                String.format(Locale.US, "%." + precision + "f", delta);
    }

    public float getPercentsDelta(double otherFrequency) {
        return (float)Math.abs(1 - (otherFrequency / frequency));
    }
}
