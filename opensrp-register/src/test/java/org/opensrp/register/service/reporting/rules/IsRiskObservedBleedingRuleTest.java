package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsRiskObservedBleedingRule;

import static org.opensrp.common.util.EasyMap.create;
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
        SafeMap fields = new SafeMap(create("riskObservedDuringANC", "bleeding pallor").map());

        assertTrue(rule.apply(fields));
    }

    @Test
    public void shouldFailWhenRiskObservedDoesNotHaveBleeding() throws Exception {
        SafeMap fields = new SafeMap(create("riskObservedDuringANC", "pallor").map());

        assertFalse(rule.apply(fields));

        fields = new SafeMap();
        fields.put("riskObservedDuringANC", null);

        assertFalse(rule.apply(fields));
    }
}
