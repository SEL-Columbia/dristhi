package org.opensrp.common.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.motechproject.util.DateUtil.inRange;

public class DateUtil {
    private static DateUtility dateUtility = new RealDate();
    public static DateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static DateFormat yyyyMMddTHHmmssSSSZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static void fakeIt(LocalDate fakeDayAsToday) {
        dateUtility = new MockDate(fakeDayAsToday);
    }

    public static LocalDate today() {
        return dateUtility.today();
    }

    public static long millis() {
        return dateUtility.millis();
    }

    public static boolean isDateWithinGivenPeriodBeforeToday(LocalDate referenceDateForSchedule, Period period) {
        return inRange(toTime(referenceDateForSchedule), toTime(today().minus(period)), toTime(today()));
    }

    private static DateTime toTime(LocalDate referenceDateForSchedule) {
        return referenceDateForSchedule.toDateTime(new LocalTime(0, 0));
    }

    /**
     * Parses dates of following formats
     * - yyyy-MM-dd
     * - yyyy-MM-dd HH:mm:ss
     * - yyyy-MM-dd'T'HH:mm:ss.SSSZ
     *
     * @return
     * @throws ParseException
     */
    public static DateTime parseDate(String date) throws ParseException {

        try {
            return new DateTime(yyyyMMddTHHmmssSSSZ.parse(date).getTime());
        } catch (ParseException e) {
        }
        try {
            return new DateTime(yyyyMMddHHmmss.parse(date).getTime());
        } catch (ParseException e) {
        }
        try {
            return new DateTime(yyyyMMdd.parse(date).getTime());
        } catch (ParseException e) {
        }


        return DateTime.parse(date);
    }

    public static String getTodayAsString() {
        Calendar now = Calendar.getInstance();
        return yyyyMMdd.format(now.getTime());
    }

    public static LocalDate tryParse(String value, LocalDate defaultValue) {
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static Date getDateFromString(String dateString) {
        Date parsed = null;
        try {
            if (dateString != null && !dateString.equals("null") && dateString.length() > 0) {
                parsed = yyyyMMddTHHmmssSSSZ.parse(dateString.trim());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsed;
    }
}

interface DateUtility {
    LocalDate today();

    long millis();
}

class RealDate implements DateUtility {
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }

    @Override
    public long millis() {
        return DateTime.now().getMillis();
    }
}

class MockDate implements DateUtility {
    private DateTime fakeDay;

    MockDate(LocalDate fakeDay) {
        this.fakeDay = fakeDay.toDateTimeAtStartOfDay();
    }

    @Override
    public LocalDate today() {
        return fakeDay.toLocalDate();
    }

    @Override
    public long millis() {
        return fakeDay.getMillis();
    }
}

