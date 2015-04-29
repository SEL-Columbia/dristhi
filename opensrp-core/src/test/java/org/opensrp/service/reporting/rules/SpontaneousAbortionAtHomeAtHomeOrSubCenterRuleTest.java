package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.common.util.EasyMap.mapOf;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;


public class SpontaneousAbortionAtHomeAtHomeOrSubCenterRuleTest {

    private SpontaneousAbortionAtHomeAtHomeOrSubCenterRule rule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        rule = new SpontaneousAbortionAtHomeAtHomeOrSubCenterRule();
    }

    @Test
    public void shouldReturnTrueWhenSpontaneousAbortionWasAtHome() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(mapOf("serviceProvidedPlace", "home")));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnTrueWhenSpontaneousAbortionWasAtSubCenter() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(mapOf("serviceProvidedPlace", "sub_center")));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenSpontaneousAbortionWasElsewhere() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(mapOf("serviceProvidedPlace", "phc")));

        assertFalse(rulePassed);

        rulePassed = rule.apply(new SafeMap(mapOf("serviceProvidedPlace", "elsewhere")));

        assertFalse(rulePassed);
    }
}
