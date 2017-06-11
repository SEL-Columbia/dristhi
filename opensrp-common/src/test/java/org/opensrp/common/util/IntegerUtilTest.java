package org.opensrp.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerUtilTest {
    IntegerUtil integerUtil;

    @Test
    public void shouldParseIntSafely() throws Exception {
        assertEquals(0, integerUtil.tryParse("", 0));
        assertEquals(2, integerUtil.tryParse(null, 2));
        assertEquals(0, integerUtil.tryParse("0", 1));
        assertEquals(3, integerUtil.tryParse("3", 3));
    }

    @Test
    public void isIntegerTestForValidInteger() throws Exception {
        assertTrue(integerUtil.isInteger("2"));
    }

    @Test
    public void isIntegerTestForInvalidInteger() throws Exception {
        assertFalse(integerUtil.isInteger("hi"));
    }

    @Test
    public void forValidIntergers() throws Exception {
        String x = integerUtil.parseValidIntegersAndDefaultInvalidOnesToEmptyString("hi");
        assertEquals("", x);
    }

    @Test
    public void forInvalidIntegers() throws Exception {
        String y = integerUtil.parseValidIntegersAndDefaultInvalidOnesToEmptyString("9");
        assertEquals("9", y);

    }


}
