package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;

public class IsPNCVisitFirstRuleTest {

    private IsPNCVisitFirstRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsPNCVisitFirstRule();
    }

    @Test
    public void shouldReturnTrueIfThePNCVisitIsTheFirstOne() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-02");
        safeMap.put("pncVisitDates", "2012-01-02");

        boolean didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfThePNCVisitIsNotTheFirstOne() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-02");
        safeMap.put("pncVisitDates", null);

        boolean didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);

        safeMap = new SafeMap();
        safeMap.put("pncVisitDate", "2012-01-02");
        safeMap.put("pncVisitDates", "2012-01-01 2012-01-02");

        didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);
    }
}
