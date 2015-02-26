package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsMotherHasFeverRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
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
