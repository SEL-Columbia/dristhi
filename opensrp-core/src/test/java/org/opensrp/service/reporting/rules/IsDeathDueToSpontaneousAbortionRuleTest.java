package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



public class IsDeathDueToSpontaneousAbortionRuleTest {

    private IsDeathDueToSpontaneousAbortionRule rule;

    @Before
    public void setUp() {
        rule = new IsDeathDueToSpontaneousAbortionRule();
    }

    @Test
    public void shouldReturnFalseWhenDeathIsNotDueToAbortion() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("closeReason", null));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("closeReason", ""));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("closeReason", "fever_infection"));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenDeathIsDueToAbortion() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("closeReason", "spontaneous_abortion"));

        assertTrue(didRuleSucceed);
    }
}
