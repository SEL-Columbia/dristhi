package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.create;

public class ThirdANCVisitHappenedOnTimeRuleTest {

    private ThirdANCVisitHappenedOnTimeRule thirdANCVisitHappenedOnTimeRule;

    @Before
    public void setUp() throws Exception {
        thirdANCVisitHappenedOnTimeRule = new ThirdANCVisitHappenedOnTimeRule();
    }

    @Test
    public void shouldReturnTrueWhenThirdANCVisitHappens28WeeksAfterLMP() {
        Map<String, String> reportFields =
                create("id", "mother id 1")
                        .put("referenceDate", "2012-01-01")
                        .put("ancVisitDate", LocalDate.parse("2012-01-01").plusWeeks(28).toString())
                        .put("ancVisitNumber", "3")
                        .map();

        assertTrue(thirdANCVisitHappenedOnTimeRule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldReturnTrueWhenThirdANCVisitHappens34WeeksAfterLMP() {

        Map<String, String> reportFields =
                create("id", "mother id 1")
                        .put("referenceDate", "2012-01-01")
                        .put("ancVisitDate", LocalDate.parse("2012-01-01").plusWeeks(34).toString())
                        .put("ancVisitNumber", "3")
                        .map();

        assertTrue(thirdANCVisitHappenedOnTimeRule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldReturnFalseWhenAnyANCVisitOtherThanThirdHappens() {
        Map<String, String> reportFields =
                create("id", "mother id 1")
                        .put("referenceDate", "2012-01-01")
                        .put("ancVisitDate", LocalDate.parse("2012-01-01").plusWeeks(34).toString())
                        .put("ancVisitNumber", "2")
                        .map();

        assertFalse(thirdANCVisitHappenedOnTimeRule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldReturnFalseWhenThirdANCVisitHappensBefore28WeeksAfterLMP() {
        Map<String, String> reportFields =
                create("id", "mother id 1")
                        .put("referenceDate", "2012-01-01")
                        .put("ancVisitDate", LocalDate.parse("2012-01-01").plusWeeks(28).minusDays(1).toString())
                        .put("ancVisitNumber", "3")
                        .map();

        assertFalse(thirdANCVisitHappenedOnTimeRule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldReturnFalseWhenThirdANCVisitHappensAfter34WeeksAfterLMP() {
        Map<String, String> reportFields =
                create("id", "mother id 1")
                        .put("referenceDate", "2012-01-01")
                        .put("ancVisitDate", LocalDate.parse("2012-01-01").plusWeeks(34).plusDays(1).toString())
                        .put("ancVisitNumber", "3")
                        .map();

        assertFalse(thirdANCVisitHappenedOnTimeRule.apply(new SafeMap(reportFields)));
    }
}
