package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.util.EasyMap.mapOf;

public class DeliveryHappenedAtSubCenterRuleTest {

    private DeliveryHappenedAtSubCenterRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new DeliveryHappenedAtSubCenterRule();
    }

    @Test
    public void shouldReturnTrueWhenDeliveryHappenedAtSubCenter() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(mapOf("deliveryPlace", "subcenter")));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenDeliveryHappenedNotAtSubCenter() throws Exception {
        boolean rulePassed = rule.apply(new SafeMap(mapOf("deliveryPlace", "home")));

        assertFalse(rulePassed);

        rulePassed = rule.apply(new SafeMap(mapOf("deliveryPlace", "phc")));

        assertFalse(rulePassed);
    }
}
