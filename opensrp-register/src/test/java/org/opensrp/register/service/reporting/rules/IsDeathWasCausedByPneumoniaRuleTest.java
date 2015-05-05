package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsDeathWasCausedByPneumoniaRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsDeathWasCausedByPneumoniaRuleTest {

    private IsDeathWasCausedByPneumoniaRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDeathWasCausedByPneumoniaRule();
    }

    @Test
    public void shouldReturnTrueIfDeathWasCausedByPneumonia() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "pneumonia").map()));

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfDeathWasNotCausedByPneumonia() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "within_24hrs").map()));

        assertFalse(didRuleApply);
    }
}
