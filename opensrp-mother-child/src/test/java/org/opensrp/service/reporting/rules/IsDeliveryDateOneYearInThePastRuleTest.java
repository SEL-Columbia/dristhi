package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsDeliveryDateOneYearInThePastRule;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class IsDeliveryDateOneYearInThePastRuleTest {

    private IsDeliveryDateOneYearInThePastRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDeliveryDateOneYearInThePastRule();
    }

    @Test
    public void shouldReturnTrueIfDeliveryDateIsLessThanOneYear() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("serviceProvidedDate", "2012-03-01");
        safeMap.put("referenceDate", "2012-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnTrueIfDeliveryDateIsNotLessThanOneYear() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("serviceProvidedDate", "2012-03-01");
        safeMap.put("referenceDate", "2011-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);
    }
}
