package io.lth.websocket.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class StringUtils {

    private final static String DATE_TIME_FORMATTER = "dd:MM:yyyy HH:mm:ss";

    public static String getCurrentDateTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        return LocalDateTime.now().format(dateTimeFormatter);
    }
}
