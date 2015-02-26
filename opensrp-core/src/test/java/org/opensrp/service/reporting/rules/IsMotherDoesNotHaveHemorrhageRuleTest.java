package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsMotherDoesNotHaveHemorrhageRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsMotherDoesNotHaveHemorrhageRuleTest {
    private IsMotherDoesNotHaveHemorrhageRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsMotherDoesNotHaveHemorrhageRule();
    }

    @Test
    public void shouldReturnTrueIfMotherDoesNotHaveHemorrhage() {
        SafeMap safeMap = new SafeMap(create("vaginalProblems", "bad_smell_lochea").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap().put("vaginalProblems", null));
        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfMotherHasHemorrhage() {

        boolean didRuleApply = rule.apply(new SafeMap(create("vaginalProblems", "heavy_bleeding").map()));
        assertFalse(didRuleApply);
    }
}
