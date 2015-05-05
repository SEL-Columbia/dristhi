package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.WomanIsDischargedWithin48HoursOfDeliveryRule;

import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;

public class WomanIsDischargedWithin48HoursOfDeliveryRuleTest {

    private WomanIsDischargedWithin48HoursOfDeliveryRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new WomanIsDischargedWithin48HoursOfDeliveryRule();
    }

    @Test
    public void shouldReturnTrueWhenWomanIsDischargedWithin48HoursOfDelivery() throws Exception {
        LocalDate deliveryDate = LocalDate.parse("2013-01-01");
        Map<String, String> reportFields =
                create("referenceDate", deliveryDate.toString())
                        .put("dischargeDate", deliveryDate.toString())
                        .map();

        assertTrue(rule.apply(new SafeMap(reportFields)));

        reportFields =
                create("referenceDate", deliveryDate.toString())
                        .put("dischargeDate", deliveryDate.plusDays(1).toString())
                        .map();

        assertTrue(rule.apply(new SafeMap(reportFields)));

        reportFields =
                create("referenceDate", deliveryDate.toString())
                        .put("dischargeDate", deliveryDate.plusDays(2).toString())
                        .map();

        assertTrue(rule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldReturnFalseWhenWomanIsDischargedLaterThen48HoursOfDelivery() throws Exception {
        LocalDate deliveryDate = LocalDate.parse("2013-01-01");
        Map<String, String> reportFields =
                create("referenceDate", deliveryDate.toString())
                        .put("dischargeDate", deliveryDate.plusDays(3).toString())
                        .map();

        assertFalse(rule.apply(new SafeMap(reportFields)));
    }
}
