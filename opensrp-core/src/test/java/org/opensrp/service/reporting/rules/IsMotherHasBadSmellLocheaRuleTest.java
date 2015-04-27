package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.opensrp.common.util.EasyMap.create;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;


public class IsMotherHasBadSmellLocheaRuleTest {
    private IsMotherHasBadSmellLocheaRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsMotherHasBadSmellLocheaRule();
    }

    @Test
    public void shouldReturnTrueIfMotherHasBadSmellLochea() {
        SafeMap safeMap = new SafeMap(create("vaginalProblems", "heavy_bleeding bad_smell_lochea").map());

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfMotherDoesNotHaveBadSmellLochea() {

        boolean didRuleApply = rule.apply(new SafeMap(create("vaginalProblems", "heavy_bleeding").map()));
        assertFalse(didRuleApply);
    }
}
