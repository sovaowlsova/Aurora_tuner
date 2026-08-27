package com.sovaowlsova.auroratuner.core.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateWorkshop {
    public static String getDateAccordingToLocalTimezone(long timestamp) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.
                ofLocalizedDateTime(Constants.NEWS_DATE_FORMAT_STYLE)
                .withLocale(Locale.getDefault());
        Instant instant = Instant.ofEpochSecond(timestamp);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        return zonedDateTime.format(dateTimeFormatter);
    }
}
