package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsSterilizationFailureRuleTest {

    IsSterilizationFailureRule rule;

    @Before
    public void setUp() {
        rule = new IsSterilizationFailureRule();
    }

    @Test
    public void shouldReturnFalseWhenThereIsNoSterilizationFailure() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("isSterilizationFailure", null));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("isSterilizationFailure", ""));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("isSterilizationFailure", "no"));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenThereIsSterilizationFailure() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("isSterilizationFailure", "yes"));

        assertTrue(didRuleSucceed);
    }
}
