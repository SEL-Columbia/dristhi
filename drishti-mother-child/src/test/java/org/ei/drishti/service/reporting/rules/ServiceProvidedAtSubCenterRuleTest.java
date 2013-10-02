package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.util.EasyMap.mapOf;

public class ServiceProvidedAtSubCenterRuleTest {

    private ServiceProvidedAtSubCenterRule serviceProvidedAtSubCenterRule;

    @Before
    public void setUp() throws Exception {
        serviceProvidedAtSubCenterRule = new ServiceProvidedAtSubCenterRule();
    }

    @Test
    public void shouldReturnTrueWhenServiceProvidedPlaceIsSubCenter() throws Exception {
        boolean rulePassed = serviceProvidedAtSubCenterRule.apply(new SafeMap(mapOf("serviceProvidedPlace", "sub_center")));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenServiceProvidedPlaceIsNotSubCenter() throws Exception {
        boolean rulePassed = serviceProvidedAtSubCenterRule.apply(new SafeMap(mapOf("serviceProvidedPlace", "phc")));

        assertFalse(rulePassed);

        rulePassed = serviceProvidedAtSubCenterRule.apply(new SafeMap(mapOf("serviceProvidedPlace", "elsewhere")));

        assertFalse(rulePassed);

        rulePassed = serviceProvidedAtSubCenterRule.apply(new SafeMap());

        assertFalse(rulePassed);
    }
}
