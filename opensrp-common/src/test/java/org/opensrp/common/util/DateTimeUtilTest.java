package org.opensrp.common.util;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;


public class DateTimeUtilTest {
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<DateTimeUtil> constructor = DateTimeUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void nowTestOfMockDateTime() throws Exception {
        LocalDateTime localDateTime = DateTimeUtil.now();
        MockDateTime mockDateTime = new MockDateTime(localDateTime);
        assertEquals(localDateTime, mockDateTime.now());

        RealDateTime realDateTime = new RealDateTime();
        assertNotSame(localDateTime, realDateTime.now());

        DateTimeUtil.fakeIt(localDateTime);
    }


}
