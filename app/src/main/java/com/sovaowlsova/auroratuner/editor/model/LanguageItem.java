package com.sovaowlsova.auroratuner.editor.model;

public class LanguageItem {
    private String locale;
    private String name;

    public LanguageItem() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
