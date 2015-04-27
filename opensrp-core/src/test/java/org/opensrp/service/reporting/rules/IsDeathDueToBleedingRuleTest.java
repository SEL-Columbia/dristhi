package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



public class IsDeathDueToBleedingRuleTest {

    private IsDeathDueToBleedingRule rule;

    @Before
    public void setUp() {
        rule = new IsDeathDueToBleedingRule();
    }

    @Test
    public void shouldReturnFalseWhenDeathIsNotDueToBleeding() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", null));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", ""));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", "fever_infection"));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenDeathIsDueToBleeding() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", "bleeding_hemorrhage"));

        assertTrue(didRuleSucceed);
    }
}
