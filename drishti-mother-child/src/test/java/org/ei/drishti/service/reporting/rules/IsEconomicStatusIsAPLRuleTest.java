package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsEconomicStatusIsAPLRuleTest {

    IsEconomicStatusIsAPLRule rule;

    @Before
    public void setUp() {
        rule = new IsEconomicStatusIsAPLRule();
    }

    @Test
    public void shouldReturnFalseWhenEconomicStatusIsNotAPL() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("economicStatus", "bpl")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnFalseWhenEconomicStatusIsAPL() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("economicStatus", "apl")));

        assertTrue(didRuleSucceed);
    }
}
