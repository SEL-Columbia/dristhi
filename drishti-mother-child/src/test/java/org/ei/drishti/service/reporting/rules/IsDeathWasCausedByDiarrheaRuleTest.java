package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;

public class IsDeathWasCausedByDiarrheaRuleTest {

    private IsDeathWasCausedByDiarrheaRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDeathWasCausedByDiarrheaRule();
    }

    @Test
    public void shouldReturnTrueIfDeathWasCausedByDiarrhea() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "diarrhea").map()));

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfDeathWasNotCausedByDiarrhea() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "within_24hrs").map()));

        assertFalse(didRuleApply);
    }
}
