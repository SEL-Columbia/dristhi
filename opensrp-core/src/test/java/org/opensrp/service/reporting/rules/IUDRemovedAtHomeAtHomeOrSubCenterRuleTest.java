package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.common.util.EasyMap.mapOf;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;


public class IUDRemovedAtHomeAtHomeOrSubCenterRuleTest {

    private IUDRemovedAtHomeAtHomeOrSubCenterRule rule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        rule = new IUDRemovedAtHomeAtHomeOrSubCenterRule();
    }

    @Test
    public void shouldReturnTrueWhenIUDWasRemovedAtHome() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(mapOf("iudRemovalPlace", "home")));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnTrueWhenIUDWasRemovedAtSubCenter() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(mapOf("iudRemovalPlace", "sub_center")));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenIUDWasRemovedElsewhere() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(mapOf("iudRemovalPlace", "phc")));

        assertFalse(rulePassed);

        rulePassed = rule.apply(new SafeMap(mapOf("iudRemovalPlace", "elsewhere")));

        assertFalse(rulePassed);
    }
}
