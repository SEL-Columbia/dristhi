package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;


public class IsChildMoreThan5YearsOldRuleTest {

    private IsChildMoreThan5YearsOldRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsChildMoreThan5YearsOldRule();
    }

    @Test
    public void shouldReturnTrueIfAgeIsMoreThan5Years() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("entityId", "child id 1");
        safeMap.put("serviceProvidedDate", "2012-03-01");
        safeMap.put("dateOfBirth", "2007-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfAgeIsNotMoreThan5Years() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("entityId", "child id 1");
        safeMap.put("serviceProvidedDate", "2012-03-01");
        safeMap.put("dateOfBirth", "2011-01-01");

        boolean didRuleApply = rule.apply(safeMap);

        assertFalse(didRuleApply);
    }
}
