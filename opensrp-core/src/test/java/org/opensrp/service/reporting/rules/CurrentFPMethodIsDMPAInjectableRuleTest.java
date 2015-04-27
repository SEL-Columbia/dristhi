package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.mapOf;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



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
