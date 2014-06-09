package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsTTBoosterDoseGivenForMotherTest {

    private IsTTBoosterDoseGivenForMother rule;

    @Before
    public void setUp() {
        rule = new IsTTBoosterDoseGivenForMother();
    }

    @Test
    public void shouldReturnTrueWhenTTBoosterDoseIsGiven() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("ttDose", "ttbooster"));

        assertTrue(didRuleSucceed);
    }

    @Test
    public void shouldReturnFalseWhenTTBoosterDoseIsNotGiven() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("ttDose", "tt1"));

        assertFalse(didRuleSucceed);
    }
}