package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsPNCVisitBetween2And7DaysOfDeliveryRule;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class IsPNCVisitBetween2And7DaysOfDeliveryRuleTest {

    private IsPNCVisitBetween2And7DaysOfDeliveryRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsPNCVisitBetween2And7DaysOfDeliveryRule();
    }

    @Test
    public void shouldReturnTrueIfThePNCVisitIsTheVisitBetween2And7DaysOfDelivery() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-04");
        safeMap.put("pncVisitDates", "2012-01-04");
        safeMap.put("referenceDate", "2012-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);

        safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-04");
        safeMap.put("pncVisitDates", "2012-01-01 2012-01-03 2012-01-04");
        safeMap.put("referenceDate", "2012-01-01");

        didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfThePNCVisitIsNotTheVisitBetween2And7DaysOfDelivery() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-01");
        safeMap.put("pncVisitDates", null);
        safeMap.put("referenceDate", "2012-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);

        safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-08");
        safeMap.put("pncVisitDates", "2012-01-01 2012-01-03 2012-01-04 2012-01-08");
        safeMap.put("referenceDate", "2012-01-01");

        didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);
    }
}
