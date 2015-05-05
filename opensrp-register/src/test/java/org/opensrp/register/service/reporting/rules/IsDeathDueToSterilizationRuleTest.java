package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsDeathDueToSterilizationRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class IsDeathDueToSterilizationRuleTest {

    private IsDeathDueToSterilizationRule rule;

    @Before
    public void setUp() {
        rule = new IsDeathDueToSterilizationRule();
    }

    @Test
    public void shouldReturnFalseWhenDeathIsNotDueToSterilization() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("isSterilizationDeath", null));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("isSterilizationDeath", ""));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("isSterilizationDeath", "no"));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenDeathIsDueToSterilization() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("isSterilizationDeath", "yes"));

        assertTrue(didRuleSucceed);
    }
}
