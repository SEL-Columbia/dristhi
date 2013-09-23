package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;


public class NewFPMethodIsCondomRuleTest {

    NewFPMethodIsCondomRule newFPMethodIsCondomRule;

    @Before
    public void setUp() {
        initMocks(this);
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
