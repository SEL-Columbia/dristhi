package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



public class IsDeathCausedByImmunizationRuleTest {

    private IsDeathCausedByImmunizationRule rule;

    @Before
    public void setUp() {
        rule = new IsDeathCausedByImmunizationRule();
    }

    @Test
    public void shouldReturnFalseWhenDeathIsNotDueToImmunization() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("isImmunizationDeath", null));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("isImmunizationDeath", ""));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("isImmunizationDeath", "no"));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenDeathIsDueToImmunization() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("isImmunizationDeath", "yes"));

        assertTrue(didRuleSucceed);
    }
}
