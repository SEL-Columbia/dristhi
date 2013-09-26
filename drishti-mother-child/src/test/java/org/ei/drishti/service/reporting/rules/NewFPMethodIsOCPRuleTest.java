package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.ei.drishti.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;


public class NewFPMethodIsOCPRuleTest {

    NewFPMethodIsOCPRule rule;

    @Before
    public void setUp() {
        initMocks(this);
        rule = new NewFPMethodIsOCPRule();
    }

    @Test
    public void shouldReturnFalseWhenNewFPMethodOfECIsNotOCP() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("newMethod", "condom")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueIfNewFPMethodOfTheECIsOCP() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("newMethod", "ocp")));

        assertTrue(didRuleSucceed);
    }
}
