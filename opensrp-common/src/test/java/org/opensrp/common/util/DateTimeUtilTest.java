package org.opensrp.common.util;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DateTimeUtilTest {

    @Test
    public void nowTestOfMockDateTime() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now();
        MockDateTime mockDateTime = new MockDateTime(localDateTime);
        assertEquals(localDateTime, mockDateTime.now());
    }

}
