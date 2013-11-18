package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.create;
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
