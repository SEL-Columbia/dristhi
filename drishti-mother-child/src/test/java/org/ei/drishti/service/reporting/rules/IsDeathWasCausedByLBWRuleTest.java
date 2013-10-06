package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.util.EasyMap.create;
import static org.junit.Assert.assertFalse;

public class IsDeathWasCausedByLBWRuleTest {

    private IsDeathWasCausedByLBWRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDeathWasCausedByLBWRule();
    }

    @Test
    public void shouldReturnTrueIfDeathWasCausedByLBW() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "lbw").map()));

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfDeathWasNotCausedByLBW() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "within_24hrs").map()));

        assertFalse(didRuleApply);
    }
}
