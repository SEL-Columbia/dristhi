package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsDeathDueToProlongedObstructedLaborRuleTest {

    private IsDeathDueToProlongedObstructedLaborRule rule;

    @Before
    public void setUp() {
        rule = new IsDeathDueToProlongedObstructedLaborRule();
    }

    @Test
    public void shouldReturnFalseWhenDeathIsNotDueToProlongedObstructedLabor() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", null));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", ""));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", "fever_infection"));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenDeathIsDueToProlongedObstructedLabor() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("maternalDeathCause", "prolonged_obstructed_labor"));

        assertTrue(didRuleSucceed);
    }
}
