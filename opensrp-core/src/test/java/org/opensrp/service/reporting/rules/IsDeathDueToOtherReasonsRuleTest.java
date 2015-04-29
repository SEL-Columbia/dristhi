package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



public class IsDeathDueToOtherReasonsRuleTest {

    private IsDeathDueToOtherReasonsRule rule;

    @Before
    public void setUp() {
        rule = new IsDeathDueToOtherReasonsRule();
    }

    @Test
    public void shouldReturnFalseWhenDeathIsNotDueToOtherReasons() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", null));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", ""));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", "bleeding_hemorrhage"));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenDeathIsDueToOtherReasons() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", "cause_not_identified"));

        assertTrue(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", "others"));

        assertTrue(didRuleSucceed);
    }
}
