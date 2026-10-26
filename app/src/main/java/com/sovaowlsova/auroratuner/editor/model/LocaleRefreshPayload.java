package com.sovaowlsova.auroratuner.editor.model;

import android.text.SpannableString;

import java.util.List;

public record LocaleRefreshPayload(List<SpannableString> availableLocales) {
}