package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.util.EasyMap.create;

public class PNCVisitHappenedLessThan24HoursAfterDeliveryRuleTest {

    private PNCVisitHappenedLessThan24HoursAfterDeliveryRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new PNCVisitHappenedLessThan24HoursAfterDeliveryRule();
    }

    @Test
    public void shouldReturnTrueWhenPNCVisitHappenedLessThan24HoursAfterDelivery() throws Exception {
        LocalDate deliveryDate = LocalDate.parse("2013-01-01");
        Map<String, String> reportFields =
                create("deliveryPlace", "home")
                        .put("referenceDate", deliveryDate.toString())
                        .put("pncVisitDate", deliveryDate.toString())
                        .map();

        assertTrue(rule.apply(new SafeMap(reportFields)));

        reportFields =
                create("deliveryPlace", "home")
                        .put("referenceDate", deliveryDate.toString())
                        .put("pncVisitDate", deliveryDate.plusDays(1).toString())
                        .map();

        assertTrue(rule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldReturnFalseWhenPNCVisitHappenedMoreThan24HoursAfterDelivery() throws Exception {
        LocalDate deliveryDate = LocalDate.parse("2013-01-01");
        Map<String, String> reportFields =
                create("deliveryPlace", "home")
                        .put("referenceDate", deliveryDate.toString())
                        .put("pncVisitDate", deliveryDate.plusDays(2).toString())
                        .map();

        assertFalse(rule.apply(new SafeMap(reportFields)));
    }
}
