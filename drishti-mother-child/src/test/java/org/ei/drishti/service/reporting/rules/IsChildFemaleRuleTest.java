package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.ei.drishti.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsChildFemaleRuleTest {

    private IsChildFemaleRule rule;

    @Before
    public void setUp() {
        rule = new IsChildFemaleRule();
    }

    @Test
    public void shouldReturnFalseWhenChildIsNotFemale() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("gender", "male")));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap(mapOf("gender", "")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenChildIsFemale() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("gender", "female")));

        assertTrue(didRuleSucceed);
    }
}
