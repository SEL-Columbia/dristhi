package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.util.EasyMap;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsRiskObservedBleedingRuleTest {

    private IsRiskObservedBleedingRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsRiskObservedBleedingRule();
    }

    @Test
    public void shouldPassWhenRiskObservedHasBleeding() throws Exception {
        Map<String, String> fields = EasyMap.create("riskObservedDuringANC", "bleeding pallor").map();

        assertTrue(rule.apply(new SafeMap(fields)));
    }

    @Test
    public void shouldFailWhenRiskObservedDoesNotHaveBleeding() throws Exception {
        Map<String, String> fields = EasyMap.create("riskObservedDuringANC", "pallor").map();

        assertFalse(rule.apply(new SafeMap(fields)));
    }
}
