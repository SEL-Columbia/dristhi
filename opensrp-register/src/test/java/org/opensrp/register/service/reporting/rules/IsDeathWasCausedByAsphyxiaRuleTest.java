package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsDeathWasCausedByAsphyxiaRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsDeathWasCausedByAsphyxiaRuleTest {

    private IsDeathWasCausedByAsphyxiaRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDeathWasCausedByAsphyxiaRule();
    }

    @Test
    public void shouldReturnTrueIfDeathWasCausedByAsphyxia() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "asphyxia").map()));

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfDeathWasNotCausedByAsphyxia() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "within_24hrs").map()));

        assertFalse(didRuleApply);
    }
}
