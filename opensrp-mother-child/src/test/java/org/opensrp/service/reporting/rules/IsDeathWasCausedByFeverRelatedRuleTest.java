package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.IsDeathWasCausedByFeverRelatedRule;

import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;


public class IsDeathWasCausedByFeverRelatedRuleTest {

    private IsDeathWasCausedByFeverRelatedRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDeathWasCausedByFeverRelatedRule();
    }

    @Test
    public void shouldReturnTrueIfDeathCauseWasFeverRelated() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "fever_related").map()));

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfDeathCauseWasNotFeverRelated() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "within_24hrs").map()));

        assertFalse(didRuleApply);
    }
}
