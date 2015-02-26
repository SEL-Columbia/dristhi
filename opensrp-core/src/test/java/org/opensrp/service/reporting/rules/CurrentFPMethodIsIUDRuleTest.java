package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.CurrentFPMethodIsIUDRule;

import static org.opensrp.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class CurrentFPMethodIsIUDRuleTest {

    CurrentFPMethodIsIUDRule rule;

    @Before
    public void setUp() {
        rule = new CurrentFPMethodIsIUDRule();
    }

    @Test
    public void shouldReturnFalseWhenNewFPMethodOfECIsNotIUD() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("currentMethod", "ocp")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueIfNewFPMethodOfTheECIsIUD() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("currentMethod", "iud")));

        assertTrue(didRuleSucceed);
    }
}
