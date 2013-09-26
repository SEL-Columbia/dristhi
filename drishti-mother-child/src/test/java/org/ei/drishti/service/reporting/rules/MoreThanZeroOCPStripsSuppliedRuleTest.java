package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoreThanZeroOCPStripsSuppliedRuleTest {

    private MoreThanZeroOCPStripsSuppliedRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new MoreThanZeroOCPStripsSuppliedRule();
    }

    @Test
    public void shouldReturnFalseWhenNoOCPStripsAreSupplied() throws Exception {
        assertFalse(rule.apply(new SafeMap().put("numberOfOCPDelivered", null)));

        assertFalse(rule.apply(new SafeMap().put("numberOfOCPDelivered", "")));
    }

    @Test
    public void shouldReturnFalseWhenZeroOCPStripsAreSupplied() throws Exception {
        assertFalse(rule.apply(new SafeMap().put("numberOfOCPDelivered", "0")));
    }

    @Test
    public void shouldReturnTrueWhenMoreThanZeroOCPStripsAreSupplied() throws Exception {
        assertTrue(rule.apply(new SafeMap().put("numberOfOCPDelivered", "1")));
    }
}
