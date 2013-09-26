package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.ei.drishti.util.EasyMap.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class PNCVisitHappenedWithin2DaysOfDeliveryRuleTest {

    private PNCVisitHappenedWithin2DaysOfDeliveryRule rule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        rule = new PNCVisitHappenedWithin2DaysOfDeliveryRule();
    }

    @Test
    public void shouldReturnTrueWhenPNCVisitHappenedWithin2DaysOfDelivery() {
        Map<String, String> reportFields =
                create("pncVisitDate", "2013-09-20")
                        .put("referenceDate", "2013-09-20")
                        .map();

        boolean didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertTrue(didRuleApplied);

        reportFields =
                create("pncVisitDate", "2013-09-21")
                        .put("referenceDate", "2013-09-20")
                        .map();

        didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertTrue(didRuleApplied);

        reportFields =
                create("pncVisitDate", "2013-09-22")
                        .put("referenceDate", "2013-09-20")
                        .map();

        didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertTrue(didRuleApplied);
    }

    @Test
    public void shouldReturnFalseWhenPNCVisitHappenedAfter2DaysOfDelivery() {
        Map<String, String> reportFields =
                create("pncVisitDate", "2013-09-23")
                        .put("referenceDate", "2013-09-20")
                        .map();

        boolean didRuleApplied = rule.apply(new SafeMap(reportFields));

        assertFalse(didRuleApplied);
    }
}
