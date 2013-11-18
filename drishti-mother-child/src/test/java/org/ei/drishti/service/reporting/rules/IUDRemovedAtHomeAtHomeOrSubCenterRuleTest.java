package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.mockito.MockitoAnnotations.initMocks;

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
