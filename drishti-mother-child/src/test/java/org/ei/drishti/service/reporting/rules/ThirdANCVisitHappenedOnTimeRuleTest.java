package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class ThirdANCVisitHappenedOnTimeRuleTest {

    private ThirdANCVisitHappenedOnTimeRule thirdANCVisitHappenedOnTimeRule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        thirdANCVisitHappenedOnTimeRule = new ThirdANCVisitHappenedOnTimeRule();
    }

    @Test
    public void shouldReturnTrueWhenThirdANCVisitHappens28WeeksAfterLMP() {
        SafeMap reportFields = new SafeMap();
        reportFields.put("id", "mother id 1");
        reportFields.put("referenceDate", "2012-01-01");
        reportFields.put("ancVisitDate", LocalDate.parse("2012-01-01").plusWeeks(28).toString());
        reportFields.put("ancVisitNumber", "3");

        assertTrue(thirdANCVisitHappenedOnTimeRule.apply(reportFields));
    }

    @Test
    public void shouldReturnTrueWhenThirdANCVisitHappens34WeeksAfterLMP() {
        SafeMap reportFields = new SafeMap();
        reportFields.put("id", "mother id 1");
        reportFields.put("referenceDate", "2012-01-01");
        reportFields.put("ancVisitDate",
                LocalDate.parse("2012-01-01").plusWeeks(34).toString());
        reportFields.put("ancVisitNumber", "3");

        assertTrue(thirdANCVisitHappenedOnTimeRule.apply(reportFields));
    }

    @Test
    public void shouldReturnFalseWhenAnyANCVisitOtherThanThirdHappens() {
        SafeMap reportFields = new SafeMap();
        reportFields.put("id", "mother id 1");
        reportFields.put("referenceDate", "2012-01-01");
        reportFields.put("ancVisitDate",
                LocalDate.parse("2012-01-01").plusWeeks(34).toString());
        reportFields.put("ancVisitNumber", "2");

        assertFalse(thirdANCVisitHappenedOnTimeRule.apply(reportFields));
    }

    @Test
    public void shouldReturnFalseWhenThirdANCVisitHappensBefore28WeeksAfterLMP() {
        SafeMap reportFields = new SafeMap();
        reportFields.put("id", "mother id 1");
        reportFields.put("referenceDate", "2012-01-01");
        reportFields.put("ancVisitDate",
                LocalDate.parse("2012-01-01").plusWeeks(28).minusDays(1).toString());
        reportFields.put("ancVisitNumber", "3");

        assertFalse(thirdANCVisitHappenedOnTimeRule.apply(reportFields));
    }

    @Test
    public void shouldReturnFalseWhenThirdANCVisitHappensAfter34WeeksAfterLMP() {
        SafeMap reportFields = new SafeMap();
        reportFields.put("id", "mother id 1");
        reportFields.put("referenceDate", "2012-01-01");
        reportFields.put("ancVisitDate",
                LocalDate.parse("2012-01-01").plusWeeks(34).plusDays(1).toString());
        reportFields.put("ancVisitNumber", "3");

        assertFalse(thirdANCVisitHappenedOnTimeRule.apply(reportFields));
    }
}
