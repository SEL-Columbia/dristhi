package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsDeathWasCausedByMeaslesRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsDeathWasCausedByMeaslesRuleTest {

    private IsDeathWasCausedByMeaslesRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDeathWasCausedByMeaslesRule();
    }

    @Test
    public void shouldReturnTrueIfDeathWasCausedByMeasles() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "measles").map()));

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfDeathWasNotCausedByMeasles() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "within_24hrs").map()));

        assertFalse(didRuleApply);
    }
}
