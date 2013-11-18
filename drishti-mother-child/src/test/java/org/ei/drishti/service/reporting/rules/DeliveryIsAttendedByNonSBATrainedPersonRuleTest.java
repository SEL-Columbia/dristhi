package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.mapOf;

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
