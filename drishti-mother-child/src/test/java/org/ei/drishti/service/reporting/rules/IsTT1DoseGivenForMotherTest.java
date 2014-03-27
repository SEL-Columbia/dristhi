package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsTT1DoseGivenForMotherTest {

    private IsTT1DoseGivenForMother rule;

    @Before
    public void setUp() {
        rule = new IsTT1DoseGivenForMother();
    }

    @Test
    public void shouldReturnTrueWhenTT1DoseIsGiven() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("ttDose", "tt1"));

        assertTrue(didRuleSucceed);
    }

    @Test
    public void shouldReturnFalseWhenTT1DoseIsNotGiven() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("ttDose", "tt2"));

        assertFalse(didRuleSucceed);
    }
}