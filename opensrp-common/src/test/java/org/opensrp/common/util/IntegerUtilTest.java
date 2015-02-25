package org.opensrp.common.util;

import org.junit.Test;
import org.opensrp.common.util.IntegerUtil;

import static org.junit.Assert.assertEquals;

public class IntegerUtilTest {
    @Test
    public void shouldParseIntSafely() throws Exception {
        assertEquals(0, IntegerUtil.tryParse("", 0));
        assertEquals(2, IntegerUtil.tryParse(null, 2));
        assertEquals(0, IntegerUtil.tryParse("0", 1));
        assertEquals(3, IntegerUtil.tryParse("3", 3));
    }
}
