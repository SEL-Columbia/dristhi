package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class IsPNCVisitHappenedWithin42DaysOfDeliveryRuleRuleTest {

    private IsPNCVisitHappenedWithin42DaysOfDeliveryRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsPNCVisitHappenedWithin42DaysOfDeliveryRule();
    }

    @Test
    public void shouldReturnTrueIfThePNCVisitHappenedWithin42DaysOfDelivery() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-04");
        safeMap.put("pncVisitDay", "42");

        boolean didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);

        safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-04");
        safeMap.put("pncVisitDay", "36");
        safeMap.put("referenceDate", "2012-01-01");

        didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfThePNCVisitHappenedAfter42DaysOfDelivery() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-04");
        safeMap.put("pncVisitDay", "46");
        safeMap.put("referenceDate", "2012-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);

    }
}
