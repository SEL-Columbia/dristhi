package org.opensrp.common.util;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;


public class DateUtilTest {
    DateUtil dateUtil;

    @Test
    public void testMillis() throws Exception {
        LocalDate today = LocalDate.now();
        long expected = today.toDate().getTime();
        long localDate = DateUtil.millis();
        System.out.println(expected + " " + localDate);
        assertNotSame(expected, localDate);
    }

    @Test
    public void testMillisFromMockDate() throws Exception {
        LocalDate today = LocalDate.now();
        long expectedTime = today.toDate().getTime();
        MockDate mockDate = new MockDate(today);
        long localDate = mockDate.millis();

        assertEquals(expectedTime, localDate);
    }


    @Test
    public void testIsDateWithinGivenPeriodBeforeToday() throws Exception {
        LocalDate referenceDateForSchedule = LocalDate.parse("2017-05-27");
        Period period = Period.days(3);
        assertEquals(false, DateUtil.isDateWithinGivenPeriodBeforeToday(referenceDateForSchedule, period));
    }

    @Test
    public void testParseDate() throws Exception {
        String yyyyMMdd = "2017-05-30";
        String yyyyMMddHHmmss = "2012-07-10 14:58:00";
        String yyyyMMddTHHmmssSSSZ = "2017-06-01T14:29:27.845+0600";

        String s = "3/24/2013 21:54";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Date date = simpleDateFormat.parse(s);
        System.out.println("date : " + simpleDateFormat.format(date));


        assertEquals(yyyyMMdd, DateUtil.yyyyMMdd.format(DateUtil.parseDate(yyyyMMdd).toDate()));
        assertEquals(yyyyMMddHHmmss, DateUtil.yyyyMMddHHmmss.format(DateUtil.parseDate(yyyyMMddHHmmss).toDate()));
        assertEquals(yyyyMMddTHHmmssSSSZ, DateUtil.yyyyMMddTHHmmssSSSZ.format(DateUtil.parseDate(yyyyMMddTHHmmssSSSZ).toDate()));
        //assertEquals(diffFormat, DateUtil.parseDate(diffFormat));
    }

    @Test
    public void testGetTodayAsString() throws Exception {
        LocalDate today = LocalDate.now();
        String formattedDate = today.toString("yyyy-MM-dd");
        assertEquals(formattedDate, DateUtil.getTodayAsString());
    }

    @Test
    public void testTryParse() {
        String value = "1994-04-03";
        LocalDate tested = LocalDate.parse(value);
        LocalDate defaultvalue = LocalDate.now();
        assertEquals(tested, DateUtil.tryParse(value, defaultvalue));
        assertNotSame(tested, DateUtil.tryParse("x", defaultvalue));
    }

    @Test
    public void testGetDateFromString() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String dateString = "2017-05-30T14:19:01.232+0600";
        String temp = formatter.format(new Date());
        Date date = null;
        date = formatter.parse(dateString);
        assertEquals(date, DateUtil.getDateFromString(dateString));

        String s = "abc invalid date";
        assertEquals(null, DateUtil.getDateFromString(s));
    }
}
