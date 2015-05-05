package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsDeathWasCausedByDiarrheaRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsDeathWasCausedByDiarrheaRuleTest {

    private IsDeathWasCausedByDiarrheaRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDeathWasCausedByDiarrheaRule();
    }

    @Test
    public void shouldReturnTrueIfDeathWasCausedByDiarrhea() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "diarrhea").map()));

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfDeathWasNotCausedByDiarrhea() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "within_24hrs").map()));

        assertFalse(didRuleApply);
    }
}
