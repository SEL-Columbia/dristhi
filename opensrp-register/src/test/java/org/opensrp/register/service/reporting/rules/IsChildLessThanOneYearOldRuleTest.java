package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsChildLessThanOneYearOldRule;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class IsChildLessThanOneYearOldRuleTest {

    private IsChildLessThanOneYearOldRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsChildLessThanOneYearOldRule();
    }

    @Test
    public void shouldReturnTrueIfAgeIsLessThanOneYear() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("entityId", "child id 1");
        safeMap.put("serviceProvidedDate", "2012-03-01");
        safeMap.put("dateOfBirth", "2012-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfAgeIsNotLessThanOneYear() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("entityId", "child id 1");
        safeMap.put("serviceProvidedDate", "2012-03-01");
        safeMap.put("dateOfBirth", "2011-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfServiceProvidedDateIsBlank() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("entityId", "child id 1");
        safeMap.put("serviceProvidedDate", "");
        safeMap.put("dateOfBirth", "2011-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);
    }
}
