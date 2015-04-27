package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.mapOf;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;

public class DeliveryHappenedAtHomeRuleTest {

    private DeliveryHappenedAtHomeRule deliveryHappenedAtHomeRule;

    @Before
    public void setUp() throws Exception {
        deliveryHappenedAtHomeRule = new DeliveryHappenedAtHomeRule();
    }

    @Test
    public void shouldReturnTrueWhenDeliveryHappenedAtHome() throws Exception {
        boolean rulePassed = deliveryHappenedAtHomeRule.apply(new SafeMap(mapOf("deliveryPlace", "home")));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenDeliveryHappenedNotAtHome() throws Exception {
        boolean rulePassed = deliveryHappenedAtHomeRule.apply(new SafeMap(mapOf("deliveryPlace", "subcenter")));

        assertFalse(rulePassed);

        rulePassed = deliveryHappenedAtHomeRule.apply(new SafeMap(mapOf("deliveryPlace", "phc")));

        assertFalse(rulePassed);
    }
}
