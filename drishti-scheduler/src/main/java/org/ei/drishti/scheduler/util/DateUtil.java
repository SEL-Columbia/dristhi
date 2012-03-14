package org.ei.drishti.scheduler.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import static org.motechproject.util.DateUtil.*;

public class DateUtil {
    public static boolean isDateWithinGivenPeriodBeforeToday(LocalDate referenceDateForSchedule, Period period) {
        return inRange(toTime(referenceDateForSchedule), toTime(today().minus(period)), toTime(today()));
    }

    private static DateTime toTime(LocalDate referenceDateForSchedule) {
        return referenceDateForSchedule.toDateTime(new LocalTime(0, 0));
    }
}
