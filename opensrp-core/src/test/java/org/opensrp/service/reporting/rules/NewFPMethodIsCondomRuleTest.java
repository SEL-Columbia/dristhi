package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



public class NewFPMethodIsCondomRuleTest {

    NewFPMethodIsCondomRule newFPMethodIsCondomRule;

    @Before
    public void setUp() {
        newFPMethodIsCondomRule = new NewFPMethodIsCondomRule();
    }

    @Test
    public void shouldReturnFalseWhenNewFPMethodOfECIsNotCondom() {
        boolean didRuleSucceed = newFPMethodIsCondomRule.apply(new SafeMap().put("newMethod", "ocp"));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueIfNewFPMethodOfTheECIsCondom() {
        boolean didRuleSucceed = newFPMethodIsCondomRule.apply(new SafeMap().put("newMethod", "condom"));

        assertTrue(didRuleSucceed);
    }
}
