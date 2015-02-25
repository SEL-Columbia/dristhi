package org.ei.drishti.common.util;

import org.junit.Assert;
import org.junit.Test;

public class DoubleUtilTest {
    @Test
    public void shouldParseIntSafely() throws Exception {
        Assert.assertEquals(0.1D, DoubleUtil.tryParse("", 0.1D), 0D);
        Assert.assertEquals(2.0D, DoubleUtil.tryParse(null, 2.0D), 0D);
        Assert.assertEquals(0D, DoubleUtil.tryParse("0", 1.1D), 0D);
        Assert.assertEquals(3D, DoubleUtil.tryParse("3", 3.2D), 0D);
    }
}
