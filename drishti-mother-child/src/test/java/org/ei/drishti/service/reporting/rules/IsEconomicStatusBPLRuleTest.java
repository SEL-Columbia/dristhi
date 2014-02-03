package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsEconomicStatusBPLRuleTest {

    IsEconomicStatusBPLRule rule;

    @Before
    public void setUp() {
        rule = new IsEconomicStatusBPLRule();
    }

    @Test
    public void shouldReturnFalseWhenEconomicStatusIsNotBPL() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("economicStatus", "apl")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnFalseWhenEconomicStatusIsBPL() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("economicStatus", "bpl")));

        assertTrue(didRuleSucceed);
    }
}
