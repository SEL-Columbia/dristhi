package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.ei.drishti.util.EasyMap.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class PNCVisitHappenedBetween2And14DaysOfDeliveryRuleTest {

    private PNCVisitHappenedBetween2And14DaysOfDeliveryRule rule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        rule = new PNCVisitHappenedBetween2And14DaysOfDeliveryRule();
    }

    @Test
    public void shouldReturnTrueWhenPNCVisitHappenedBetween2And14DaysOfDelivery() {
        LocalDate deliveryDate = LocalDate.parse("2013-09-20");
        Map<String, String> reportFields =
                create("pncVisitDate", deliveryDate.plusDays(3).toString())
                        .put("referenceDate", deliveryDate.toString())
                        .map();

        boolean didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertTrue(didRuleApplied);

        reportFields =
                create("pncVisitDate", deliveryDate.plusDays(13).toString())
                        .put("referenceDate", deliveryDate.toString())
                        .map();

        didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertTrue(didRuleApplied);

        reportFields =
                create("pncVisitDate", deliveryDate.plusDays(14).toString())
                        .put("referenceDate", deliveryDate.toString())
                        .map();

        didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertTrue(didRuleApplied);
    }

    @Test
    public void shouldReturnFalseWhenPNCVisitNotHappenedBetween2And14DaysOfDelivery() {
        LocalDate deliveryDate = LocalDate.parse("2013-09-20");
        Map<String, String> reportFields =
                create("pncVisitDate", deliveryDate.plusDays(1).toString())
                        .put("referenceDate", deliveryDate.toString())
                        .map();

        boolean didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertFalse(didRuleApplied);

        reportFields =
                create("pncVisitDate", deliveryDate.plusDays(2).toString())
                        .put("referenceDate", deliveryDate.toString())
                        .map();

        didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertFalse(didRuleApplied);

        reportFields =
                create("pncVisitDate", deliveryDate.plusDays(15).toString())
                        .put("referenceDate", deliveryDate.toString())
                        .map();

        didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertFalse(didRuleApplied);
    }
}
