package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsChildMaleRule;

import static org.opensrp.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class IsChildMaleRuleTest {

    private IsChildMaleRule rule;

    @Before
    public void setUp() {
        rule = new IsChildMaleRule();
    }

    @Test
    public void shouldReturnFalseWhenChildIsNotMale() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("gender", "female")));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap(mapOf("gender", "")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenChildIsMale() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("gender", "male")));

        assertTrue(didRuleSucceed);
    }
}
