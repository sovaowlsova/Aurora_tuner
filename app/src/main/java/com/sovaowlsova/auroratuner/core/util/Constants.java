package com.sovaowlsova.auroratuner.core.util;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;

import androidx.core.content.ContextCompat;

import java.time.format.FormatStyle;
import java.util.List;
import java.util.Map;
import com.sovaowlsova.auroratuner.R;

public class Constants {
    public static final String NEWS_URL = "https://sovaowlsova.github.io/auroratuner-github.io/news.json";
    public static final int SAMPLE_RATE = 44100;
    public static final int BUFFER_SIZE = 8192;
    public static final FormatStyle NEWS_DATE_FORMAT_STYLE = FormatStyle.MEDIUM;
    public static final Map<Integer, Integer> httpCodeToStringId = Map.of(
            400, R.string.error_http_400,
            401, R.string.error_http_401,
            403, R.string.error_http_403,
            404, R.string.error_http_404,
            408, R.string.error_http_408,
            429, R.string.error_http_429,
            500, R.string.error_http_500,
            502, R.string.error_http_502,
            503, R.string.error_http_503,
            504, R.string.error_http_504
    );
    public static final List<String> supportedLocales = List.of(
            "en \uD83C\uDDFA\uD83C\uDDF8",
            "be \uD83C\uDDE7\uD83C\uDDFE",
            "cs \uD83C\uDDE8\uD83C\uDDFF",
            "de \uD83C\uDDE9\uD83C\uDDEA",
            "es \uD83C\uDDEA\uD83C\uDDF8",
            "ru \uD83C\uDDF7\uD83C\uDDFA",
            "tt [TT]",
            "uk \uD83C\uDDFA\uD83C\uDDE6"
    );
}
