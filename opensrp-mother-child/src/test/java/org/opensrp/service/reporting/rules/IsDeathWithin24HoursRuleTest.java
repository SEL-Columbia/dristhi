package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsDeathWithin24HoursRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsDeathWithin24HoursRuleTest {

    private IsDeathWithin24HoursRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDeathWithin24HoursRule();
    }

    @Test
    public void shouldReturnTrueIfDeathOccursWithin24Hours() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "within_24hrs").map()));

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfDeathDoesNotOccurWithin24Hours() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "sepsis").map()));

        assertFalse(didRuleApply);
    }
}
