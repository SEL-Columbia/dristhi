package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.ei.drishti.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class NewFPMethodIsCentchromanPillsRuleTest {

    NewFPMethodIsCentchromanPillsRule rule;

    @Before
    public void setUp() {
        rule = new NewFPMethodIsCentchromanPillsRule();
    }

    @Test
    public void shouldReturnFalseWhenNewFPMethodOfECIsNotCentchroman() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("newMethod", "condom")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueIfNewFPMethodOfTheECIsCentchroman() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("newMethod", "centchroman")));

        assertTrue(didRuleSucceed);
    }
}
