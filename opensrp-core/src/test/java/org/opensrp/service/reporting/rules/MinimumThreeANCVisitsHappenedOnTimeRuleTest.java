package org.opensrp.service.reporting.rules;

import org.opensrp.domain.Mother;
import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.create;
import static org.opensrp.common.util.EasyMap.mapOf;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.repository.AllMothers;
import org.opensrp.service.reporting.rules.MinimumThreeANCVisitsHappenedOnTimeRule;

public class MinimumThreeANCVisitsHappenedOnTimeRuleTest {

    private MinimumThreeANCVisitsHappenedOnTimeRule thirdANCVisitHappenedOnTimeRule;
    @Mock
    private AllMothers mothers;
    @Mock
    private Mother mother;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        thirdANCVisitHappenedOnTimeRule = new MinimumThreeANCVisitsHappenedOnTimeRule(mothers);
    }

    @Test
    public void shouldReturnTrueWhenThirdANCVisitHappensBetween28WeeksAnd40WeeksAfterLMP() {
        Map<String, String> reportFields =
                create("id", "mother id 1")
                        .put("referenceDate", "2013-01-01")
                        .put("ancVisitDate", "2013-07-16")
                        .map();

        when(mothers.findByCaseId("mother id 1")).thenReturn(mother);
        List<Map<String, String>> ancVisits = asList(mapOf("ancVisit1", "1"), mapOf("ancVisit2", "2"), mapOf("ancVisit3", "3"));
        when(mother.ancVisits()).thenReturn(ancVisits);
        assertTrue(thirdANCVisitHappenedOnTimeRule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldNotReportTheFourthANCVisit() {
        Map<String, String> reportFields =
                create("id", "mother id 1")
                        .put("referenceDate", "2013-01-01")
                        .put("ancVisitDate", "2013-10-08")
                        .map();

        when(mothers.findByCaseId("mother id 1")).thenReturn(mother);
        List<Map<String, String>> ancVisits = asList(mapOf("ancVisit1", "1"), mapOf("ancVisit2", "2"), mapOf("ancVisit3", "3"), mapOf("ancVisit4", "4"));
        when(mother.ancVisits()).thenReturn(ancVisits);
        assertFalse(thirdANCVisitHappenedOnTimeRule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldReturnFalseWhenAnLessThanThreeANCVisitsHasHappened() {
        Map<String, String> reportFields =
                create("id", "mother id 1")
                        .put("referenceDate", "2013-01-01")
                        .put("ancVisitDate", "2013-07-16")
                        .map();

        when(mothers.findByCaseId("mother id 1")).thenReturn(mother);
        List<Map<String, String>> ancVisits = asList(mapOf("ancVisit1", "1"), mapOf("ancVisit2", "2"));
        when(mother.ancVisits()).thenReturn(ancVisits);
        assertFalse(thirdANCVisitHappenedOnTimeRule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldReturnFalseWhenThirdANCVisitHappensBefore28WeeksAfterLMP() {
        Map<String, String> reportFields =
                create("id", "mother id 1")
                        .put("referenceDate", "2013-01-01")
                        .put("ancVisitDate", "2013-07-15")
                        .map();

        when(mothers.findByCaseId("mother id 1")).thenReturn(mother);
        List<Map<String, String>> ancVisits = asList(mapOf("ancVisit1", "1"), mapOf("ancVisit2", "2"), mapOf("ancVisit3", "3"));
        when(mother.ancVisits()).thenReturn(ancVisits);
        assertFalse(thirdANCVisitHappenedOnTimeRule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldReturnFalseWhenThirdANCVisitHappensAfter34WeeksAfterLMP() {
        Map<String, String> reportFields =
                create("id", "mother id 1")
                        .put("referenceDate", "2013-01-01")
                        .put("ancVisitDate", "2013-10-09")
                        .map();

        when(mothers.findByCaseId("mother id 1")).thenReturn(mother);
        List<Map<String, String>> ancVisits = asList(mapOf("ancVisit1", "1"), mapOf("ancVisit2", "2"), mapOf("ancVisit3", "3"));
        when(mother.ancVisits()).thenReturn(ancVisits);
        assertFalse(thirdANCVisitHappenedOnTimeRule.apply(new SafeMap(reportFields)));
    }
}
