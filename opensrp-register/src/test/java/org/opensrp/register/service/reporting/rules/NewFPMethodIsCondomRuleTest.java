package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.NewFPMethodIsCondomRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



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
