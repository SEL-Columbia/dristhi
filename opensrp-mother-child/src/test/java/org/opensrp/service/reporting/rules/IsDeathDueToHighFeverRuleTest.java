package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsDeathDueToHighFeverRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class IsDeathDueToHighFeverRuleTest {

    private IsDeathDueToHighFeverRule rule;

    @Before
    public void setUp() {
        rule = new IsDeathDueToHighFeverRule();
    }

    @Test
    public void shouldReturnFalseWhenDeathIsNotDueToHighFever() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", null));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", ""));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", "bleeding_hemorrhage"));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenDeathIsDueToHighFever() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", "fever_infection"));

        assertTrue(didRuleSucceed);
    }
}
