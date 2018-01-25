package xyz.jienan.refreshed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by jienanzhang on 16/07/2017.
 */

public class TimeUtils {

    private static final String REG_TIME_UTC_Z = "^\\d{4}-\\d{2}-\\d{2}[T]\\d{2}:\\d{2}:\\d{2}[Z]$"; // 2018-01-08T18:13:00Z
    private static final String REG_TIME_UTC = "^\\d{4}-\\d{2}-\\d{2}[T]\\d{2}:\\d{2}:\\d{2}$"; // 2018-01-08T18:13:00
    private static final String REG_TIME_UTC_PLUS = "^\\d{4}-\\d{2}-\\d{2}[T]\\d{2}:\\d{2}:\\d{2}[+]\\d{2}:\\d{2}$"; // 2018-01-08T18:13:00+00:00
    private static final String REG_TIME_UTC_MICRO_Z = "^\\d{4}-\\d{2}-\\d{2}[T]\\d{2}:\\d{2}:\\d{2}[.]\\d{3,}[Z]$"; // 2018-01-12T21:07:02.2448015Z

    private static final String PATTERN_UTC_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String PATTERN_UTC = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String PATTERN_UTC_PLUS = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String PATTERN_UTC_MICRO_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";

    private static Pattern pUtcZ = Pattern.compile(REG_TIME_UTC_Z);
    private static Pattern pUtc = Pattern.compile(REG_TIME_UTC);
    private static Pattern pUtcPlus = Pattern.compile(REG_TIME_UTC_PLUS);
    private static Pattern pUtcMicroZ = Pattern.compile(REG_TIME_UTC_MICRO_Z);


    public static String convertTimeToString(String utcTime) {
        Date date = convertStringToDate(utcTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Date convertStringToDate(String time) {
        SimpleDateFormat sdf = parseTime(time);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = sdf.parse(time);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
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
        }

        return sdf;
    }
}
