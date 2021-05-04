package utility;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * This class provides functionality of getting timestamps and for converting timestamps to a number
 * for easy comparision
 */
public class DateTime {
    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMAT);

    /**
     * Generates a timestamp of the current time
     * @return String of the timestamp
     */
    public static String getDateTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(FORMATTER);
    }

    /**
     * Gives the current year
     * @return the current year as an int
     */
    public static int getCurrentYear() {
        DateTimeFormatter yearFormat = DateTimeFormatter.ofPattern("yyyy");
        LocalDateTime localDateTime = LocalDateTime.now();
        return Integer.parseInt(localDateTime.format(yearFormat));
    }

    /**
     * Compares two timestamps to determine which one occurred later
     * @param laterTime timestamp expected to have occurred later
     * @param soonerTime timestamp expected to have occurred earlier
     * @return true if laterTime occurs after, false if they are not or they are the same time
     */
    public static boolean isLaterThan(String laterTime, String soonerTime) {
        return stringToLong(laterTime) > stringToLong(soonerTime);
    }

    /**
     * Converts a timestamp to a long value
     * @param dateTimeStr timestamp to convert
     * @return long value representing the timestamp
     */
    public static long stringToLong(String dateTimeStr) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr,DateTimeFormatter.ofPattern(FORMAT));
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    /**
     * Converts a long value to a timestamp
     * @param time value to convert
     * @return String containing the timestamp the long value represented
     */
    public static String longToDateTime(long time){
        return LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC).format(FORMATTER);
    }
}
