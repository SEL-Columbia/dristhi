package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;

public class IsMotherHasFeverRuleTest {
    private IsMotherHasFeverRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsMotherHasFeverRule();
    }

    @Test
    public void shouldReturnTrueIfMotherHasFever() {
        SafeMap safeMap = new SafeMap(create("hasFever", "High Fever").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfMotherDoesNotHaveFever() {
        boolean didRuleApply = rule.apply(new SafeMap(create("hasFever", "").map()));
        assertFalse(didRuleApply);
    }
}
