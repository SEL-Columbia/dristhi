package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.mapOf;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;

public class ServiceProvidedAtSubCenterRuleTest {

    private ServiceProvidedAtSubCenterRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new ServiceProvidedAtSubCenterRule();
    }

    @Test
    public void shouldReturnTrueWhenServiceProvidedPlaceIsSubCenter() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(mapOf("serviceProvidedPlace", "subcenter")));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenServiceProvidedPlaceIsNotSubCenter() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(mapOf("serviceProvidedPlace", "phc")));

        assertFalse(rulePassed);

        rulePassed = rule.apply(new SafeMap(mapOf("serviceProvidedPlace", "elsewhere")));

        assertFalse(rulePassed);

        rulePassed = rule.apply(new SafeMap());

        assertFalse(rulePassed);
    }
}
