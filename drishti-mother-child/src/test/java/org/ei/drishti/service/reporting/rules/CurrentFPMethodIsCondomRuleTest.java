package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;


public class CurrentFPMethodIsCondomRuleTest {

    CurrentFPMethodIsCondomRule currentFPMethodIsCondomRule;

    @Before
    public void setUp() {
        initMocks(this);
        currentFPMethodIsCondomRule = new CurrentFPMethodIsCondomRule();
    }

    @Test
    public void shouldReturnFalseWhenCurrentFPMethodOfECIsNotCondom() {
        boolean didRuleSucceed = currentFPMethodIsCondomRule.apply(new SafeMap().put("currentMethod", "ocp"));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueIfCurrentFPMethodOfTheECIsCondom() {
        boolean didRuleSucceed = currentFPMethodIsCondomRule.apply(new SafeMap().put("currentMethod", "condom"));

        assertTrue(didRuleSucceed);
    }
}
