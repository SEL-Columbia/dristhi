package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.util.EasyMap.create;
import static org.junit.Assert.assertFalse;

public class IsDeathWasCausedByAsphyxiaRuleTest {

    private IsDeathWasCausedByAsphyxiaRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDeathWasCausedByAsphyxiaRule();
    }

    @Test
    public void shouldReturnTrueIfDeathWasCausedByAsphyxia() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "asphyxia").map()));

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfDeathWasNotCausedByAsphyxia() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "within_24hrs").map()));

        assertFalse(didRuleApply);
    }
}
