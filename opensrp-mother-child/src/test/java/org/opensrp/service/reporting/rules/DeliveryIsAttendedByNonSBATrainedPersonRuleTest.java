package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.DeliveryIsAttendedByNonSBATrainedPersonRule;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.mapOf;

public class DeliveryIsAttendedByNonSBATrainedPersonRuleTest {

    private DeliveryIsAttendedByNonSBATrainedPersonRule deliveryIsAttendedByNonSBATrainedPersonRule;

    @Before
    public void setUp() throws Exception {
        deliveryIsAttendedByNonSBATrainedPersonRule = new DeliveryIsAttendedByNonSBATrainedPersonRule();
    }

    @Test
    public void shouldReturnTrueWhenDeliveryIsAttendedByNonSBATrainedPerson() throws Exception {
        boolean rulePassed = deliveryIsAttendedByNonSBATrainedPersonRule.apply(new SafeMap(mapOf("isSkilledDelivery", "no")));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenDeliveryIsNotAttendedByNonSBATrainedPerson() throws Exception {
        boolean rulePassed = deliveryIsAttendedByNonSBATrainedPersonRule.apply(new SafeMap(mapOf("isSkilledDelivery", "yes")));

        assertFalse(rulePassed);

        rulePassed = deliveryIsAttendedByNonSBATrainedPersonRule.apply(new SafeMap(mapOf("isSkilledDelivery", "")));

        assertFalse(rulePassed);
    }
}
