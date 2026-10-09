package com.sovaowlsova.auroratuner.core.model;

public enum FragmentTag {
    TUNER("tuner"),
    PERMISSION("permission"),
    EDITOR("editor"),
    NEWS("news"),
    SETTINGS("settings");

    private final String name;

    FragmentTag(String name) {
        this.name = name;
    }

    public String get() {
        return name;
    }

    public static FragmentTag from(String tag) {
        for (FragmentTag t : values()) {
            if (t.get().equals(tag)) {
                return t;
            }
        }
        return null;
    }
}
