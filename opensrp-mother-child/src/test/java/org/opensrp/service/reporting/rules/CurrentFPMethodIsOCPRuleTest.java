package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.CurrentFPMethodIsOCPRule;

import static org.opensrp.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class CurrentFPMethodIsOCPRuleTest {

    CurrentFPMethodIsOCPRule rule;

    @Before
    public void setUp() {
        rule = new CurrentFPMethodIsOCPRule();
    }

    @Test
    public void shouldReturnFalseWhenNewFPMethodOfECIsNotOCP() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("currentMethod", "condom")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueIfNewFPMethodOfTheECIsOCP() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("currentMethod", "ocp")));

        assertTrue(didRuleSucceed);
    }
}
