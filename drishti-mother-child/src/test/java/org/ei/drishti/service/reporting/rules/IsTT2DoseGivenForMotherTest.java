package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsTT2DoseGivenForMotherTest {

    private IsTT2DoseGivenForMother rule;

    @Before
    public void setUp() {
        rule = new IsTT2DoseGivenForMother();
    }

    @Test
    public void shouldReturnTrueWhenTT2DoseIsGiven() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("ttDose", "tt2"));

        assertTrue(didRuleSucceed);
    }

    @Test
    public void shouldReturnFalseWhenTT2DoseIsNotGiven() {
        boolean didRuleSucceed = rule.apply(new SafeMap().put("ttDose", "tt1"));

        assertFalse(didRuleSucceed);
    }
}