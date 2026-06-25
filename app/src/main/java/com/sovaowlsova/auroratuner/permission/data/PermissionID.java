package com.sovaowlsova.auroratuner.permission.data;

public enum PermissionID {
    REQUEST_RECORD_AUDIO_PERMISSION(1);
    private final int id;
    private PermissionID(int id) {
        this.id = id;
    }

    public int get() {
        return id;
    }
}
