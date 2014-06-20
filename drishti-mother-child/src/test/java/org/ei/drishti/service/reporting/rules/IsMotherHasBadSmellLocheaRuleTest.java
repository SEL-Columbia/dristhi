package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;

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
