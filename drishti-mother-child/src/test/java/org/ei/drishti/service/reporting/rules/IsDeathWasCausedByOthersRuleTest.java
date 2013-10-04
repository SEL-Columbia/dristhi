package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.util.EasyMap.create;
import static org.junit.Assert.assertFalse;

public class IsDeathWasCausedByOthersRuleTest {

    private IsDeathWasCausedByOthersRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsDeathWasCausedByOthersRule();
    }

    @Test
    public void shouldReturnTrueIfDeathWasCausedByOtherReasons() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "malnutrition").map()));

        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("deathCause", "ari").map()));

        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("deathCause", "malaria").map()));

        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("deathCause", "cause_not_identified").map()));

        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(create("deathCause", "others").map()));

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfDeathWasNotCausedByOtherReasons() {

        boolean didRuleApply = rule.apply(new SafeMap(create("deathCause", "within_24hrs").map()));

        assertFalse(didRuleApply);
    }
}
