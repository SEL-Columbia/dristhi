package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;

public class IsPNCVisit48HoursOfDeliveryRuleTest {

    private IsPNCVisit48HoursOfDeliveryRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsPNCVisit48HoursOfDeliveryRule();
    }

    @Test
    public void shouldReturnTrueIfThePNCVisitIsTheVisitHappenedBefore48HoursOfDelivery() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-01");
        safeMap.put("referenceDate", "2012-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);

        safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-02");
        safeMap.put("referenceDate", "2012-01-01");

        didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfThePNCVisitIsNotTheVisit48HoursOfDelivery() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-04");
        safeMap.put("pncVisitDates", null);
        safeMap.put("referenceDate", "2012-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);

        safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-06");
        safeMap.put("referenceDate", "2012-01-01");

        didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);
    }
}
