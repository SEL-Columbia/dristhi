package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsMotherHasHemorrhageRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsMotherHasHemorrhageRuleTest {
    private IsMotherHasHemorrhageRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsMotherHasHemorrhageRule();
    }

    @Test
    public void shouldReturnTrueIfMotherHasHemorrhage() {
        SafeMap safeMap = new SafeMap(create("vaginalProblems", "heavy_bleeding bad_smell_lochea").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfMotherDoesNotHaveHemorrhage() {

        boolean didRuleApply = rule.apply(new SafeMap(create("vaginalProblems", "bad_smell_lochea").map()));
        assertFalse(didRuleApply);
    }
}
