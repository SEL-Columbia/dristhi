package org.opensrp.common.util;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DateTimeUtilTest {
    @Test
    public void nowTestOfRealDateTime() throws Exception {
        RealDateTime realDateTime = new RealDateTime();
        System.out.println(realDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now();
        assertEquals(localDateTime, realDateTime.now());
    }

    @Test
    public void nowTestOfMockDateTime() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now();
        MockDateTime mockDateTime = new MockDateTime(localDateTime);
        System.out.println(mockDateTime.now());
        assertEquals(localDateTime, mockDateTime.now());
    }

    @Test
    public void nowTestOfDateTimeUtil() throws Exception {
        DateTimeUtil dateTimeUtil = new DateTimeUtil();
        System.out.println(dateTimeUtil.now());
        LocalDateTime localDateTime = LocalDateTime.now();
        assertEquals(localDateTime, dateTimeUtil.now());
    }
}
