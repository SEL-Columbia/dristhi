package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class IsChildLessThanFourWeeksOldRuleTest {

    private IsChildLessThanFourWeeksOldRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsChildLessThanFourWeeksOldRule();
    }

    @Test
    public void shouldReturnTrueIfAgeIsLessThanFourWeeks() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("entityId", "child id 1");
        safeMap.put("serviceProvidedDate", "2012-01-28");
        safeMap.put("dateOfBirth", "2012-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfAgeIsNotLessThanFourWeeks() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("entityId", "child id 1");
        safeMap.put("serviceProvidedDate", "2012-01-31");
        safeMap.put("dateOfBirth", "2012-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);

        safeMap.put("entityId", "child id 1");
        safeMap.put("serviceProvidedDate", "2011-02-01");
        safeMap.put("dateOfBirth", "2011-01-01");

        didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);
    }
}
