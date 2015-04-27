package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.mapOf;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



public class IsEconomicStatusAPLRuleTest {

    IsEconomicStatusAPLRule rule;

    @Before
    public void setUp() {
        rule = new IsEconomicStatusAPLRule();
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
