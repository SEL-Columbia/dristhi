package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsPNCVisitFirstBetween2And14DaysOfDeliveryRule;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class IsPNCVisitFirstBetween2And14DaysOfDeliveryRuleTest {

    private IsPNCVisitFirstBetween2And14DaysOfDeliveryRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsPNCVisitFirstBetween2And14DaysOfDeliveryRule();
    }

    @Test
    public void shouldReturnTrueIfThePNCVisitIsTheFirstVisitBetween2And14DaysOfDelivery() {
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
    public void shouldReturnFalseIfThePNCVisitIsNotTheFirstVisitBetween2And14DaysOfDelivery() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-04");
        safeMap.put("pncVisitDates", null);
        safeMap.put("referenceDate", "2012-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);

        safeMap.put("pncVisitDate", "2012-01-05");
        safeMap.put("pncVisitDates", "2012-01-01 2012-01-03 2012-01-04 2012-01-05");
        safeMap.put("referenceDate", "2012-01-01");

        didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);
    }
}
