package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



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