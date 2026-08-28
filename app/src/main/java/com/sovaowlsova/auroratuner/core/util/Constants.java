package com.sovaowlsova.auroratuner.core.util;

import java.time.format.FormatStyle;
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
}
