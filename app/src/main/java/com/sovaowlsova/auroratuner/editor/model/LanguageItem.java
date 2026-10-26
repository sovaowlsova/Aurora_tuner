package com.sovaowlsova.auroratuner.editor.model;

import android.text.SpannableString;

public class LanguageItem {
    private String locale;
    private String name;

    public LanguageItem() {}
    public LanguageItem(LanguageItem other) {
        this.name = other.getName();
        this.locale = other.getLocale();
    }

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
