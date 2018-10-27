package xyz.jienan.refreshed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * Created by jienanzhang on 16/07/2017.
 */

public class TimeUtils {

    private static final String REG_TIME_UTC_Z = "^\\d{4}-\\d{2}-\\d{2}[T]\\d{2}:\\d{2}:\\d{2}[Z]$"; // 2018-01-08T18:13:00Z
    private static final String REG_TIME_UTC = "^\\d{4}-\\d{2}-\\d{2}[T]\\d{2}:\\d{2}:\\d{2}$"; // 2018-01-08T18:13:00
    private static final String REG_TIME_UTC_PLUS = "^\\d{4}-\\d{2}-\\d{2}[T]\\d{2}:\\d{2}:\\d{2}[+]\\d{2}:\\d{2}$"; // 2018-01-08T18:13:00+00:00
    private static final String REG_TIME_UTC_MICRO_Z = "^\\d{4}-\\d{2}-\\d{2}[T]\\d{2}:\\d{2}:\\d{2}[.]\\d{3,}[Z]$"; // 2018-01-12T21:07:02.2448015Z
    private static final String REG_TIME_UTC_MICRO = "^\\d{4}-\\d{2}-\\d{2}[T]\\d{2}:\\d{2}:\\d{2}[.]\\d{3,}[+]\\d{2}:\\d{2}$";

    private static final String PATTERN_UTC_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String PATTERN_UTC = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String PATTERN_UTC_PLUS = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String PATTERN_UTC_MICRO_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";
    private static final String PATTERN_UTC_MICRO = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ";

    private static Pattern pUtcZ = Pattern.compile(REG_TIME_UTC_Z);
    private static Pattern pUtc = Pattern.compile(REG_TIME_UTC);
    private static Pattern pUtcPlus = Pattern.compile(REG_TIME_UTC_PLUS);
    private static Pattern pUtcMicroZ = Pattern.compile(REG_TIME_UTC_MICRO_Z);
    private static Pattern pUtcMicro = Pattern.compile(REG_TIME_UTC_MICRO);

    public static String getNewsAgedFrom(int newsAgeInDays) {
        Date date = new Date(new Date().getTime() - newsAgeInDays * 24 * 60 * 60 * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static String convertTimeToString(String utcTime) {
        Date date = convertStringToDate(utcTime);
        if (date == null) {
            return utcTime;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    public static Date convertStringToDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        try {
            sdf = parseTime(time);
        } catch (IllegalArgumentException e) {
            Timber.e(e);
            return null;
        }

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = sdf.parse(time);
            return date;
        } catch (ParseException e) {
            Timber.e(e);
            return null;
        }
    }

    private static SimpleDateFormat parseTime(final String time) {
        SimpleDateFormat sdf = new SimpleDateFormat();

        if (pUtcZ.matcher(time).matches()) {
            sdf.applyPattern(PATTERN_UTC_Z);
        } else if (pUtc.matcher(time).matches()) {
            sdf.applyPattern(PATTERN_UTC);
        } else if (pUtcPlus.matcher(time).matches()) {
            sdf.applyPattern(PATTERN_UTC_PLUS);
        } else if (pUtcMicroZ.matcher(time).matches()) {
            sdf.applyPattern(PATTERN_UTC_MICRO_Z);
        } else if (pUtcMicro.matcher(time).matches()) {
            sdf.applyPattern(PATTERN_UTC_MICRO);
        }

        return sdf;
    }
}
