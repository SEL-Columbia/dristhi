package org.opensrp.service.reporting.rules;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.common.util.EasyMap.mapOf;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;


public class DeliveryIsAttendedBySBATrainedPersonRuleTest {

    private DeliveryIsAttendedBySBATrainedPersonRule deliveryIsAttendedBySBATrainedPersonRule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        deliveryIsAttendedBySBATrainedPersonRule = new DeliveryIsAttendedBySBATrainedPersonRule();
    }

    @Test
    public void shouldReturnTrueWhenDeliveryIsAttendedBySBATrainedPerson() throws Exception {
        boolean rulePassed = deliveryIsAttendedBySBATrainedPersonRule.apply(new SafeMap(mapOf("isSkilledDelivery", "yes")));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenDeliveryIsNotAttendedBySBATrainedPerson() throws Exception {
        boolean rulePassed = deliveryIsAttendedBySBATrainedPersonRule.apply(new SafeMap(mapOf("isSkilledDelivery", "no")));

        assertFalse(rulePassed);

        rulePassed = deliveryIsAttendedBySBATrainedPersonRule.apply(new SafeMap(mapOf("isSkilledDelivery", "")));

        assertFalse(rulePassed);
    }
}
