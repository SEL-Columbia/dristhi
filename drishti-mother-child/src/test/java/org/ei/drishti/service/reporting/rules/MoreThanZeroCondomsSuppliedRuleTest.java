package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoreThanZeroCondomsSuppliedRuleTest {

    private MoreThanZeroCondomsSuppliedRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new MoreThanZeroCondomsSuppliedRule();
    }

    @Test
    public void shouldReturnFalseWhenNoCondomsAreSupplied() throws Exception {
        assertFalse(rule.apply(new SafeMap().put("numberOfCondomsSupplied", null)));

        assertFalse(rule.apply(new SafeMap().put("numberOfCondomsSupplied", "")));
    }

    @Test
    public void shouldReturnFalseWhenZeroCondomsAreSupplied() throws Exception {
        assertFalse(rule.apply(new SafeMap().put("numberOfCondomsSupplied", "0")));
    }

    @Test
    public void shouldReturnTrueWhenMoreThanZeroCondomsAreSupplied() throws Exception {
        assertTrue(rule.apply(new SafeMap().put("numberOfCondomsSupplied", "1")));
    }
}
