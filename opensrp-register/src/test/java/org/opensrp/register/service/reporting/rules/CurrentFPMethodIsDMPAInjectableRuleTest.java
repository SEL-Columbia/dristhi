package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.CurrentFPMethodIsDMPAInjectableRule;

import static org.opensrp.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class CurrentFPMethodIsDMPAInjectableRuleTest {

    CurrentFPMethodIsDMPAInjectableRule rule;

    @Before
    public void setUp() {
        rule = new CurrentFPMethodIsDMPAInjectableRule();
    }

    @Test
    public void shouldReturnFalseWhenNewFPMethodOfECIsNotDMPAInjectable() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("currentMethod", "ocp")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueIfNewFPMethodOfTheECIsDMPAInjectable() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("currentMethod", "dmpa_injectable")));

        assertTrue(didRuleSucceed);
    }
}
