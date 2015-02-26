package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.CurrentFPMethodIsMaleSterilizationRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class CurrentFPMethodIsMaleSterilizationRuleTest {

    CurrentFPMethodIsMaleSterilizationRule rule;

    @Before
    public void setUp() {
        rule = new CurrentFPMethodIsMaleSterilizationRule();
    }

    @Test
    public void shouldReturnFalseWhenCurrentFPMethodOfECIsNotMaleSterilization() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("currentMethod", "ocp"));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueIfCurrentFPMethodOfTheECIsMaleSterilization() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("currentMethod", "male_sterilization"));

        assertTrue(didRuleSucceed);
    }
}
