package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.ei.drishti.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class CurrentFPMethodIsCentchromanRuleTest {

    CurrentFPMethodIsCentchromanRule rule;

    @Before
    public void setUp() {
        rule = new CurrentFPMethodIsCentchromanRule();
    }

    @Test
    public void shouldReturnFalseWhenNewFPMethodOfECIsNotCentchroman() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("currentMethod", "ocp")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueIfNewFPMethodOfTheECIsCentchroman() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("currentMethod", "centchroman")));

        assertTrue(didRuleSucceed);
    }
}
