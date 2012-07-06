package org.ei.drishti.service;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.service.reporting.ReportingService;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.testing.utils.BaseUnitTest;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotherReportingServiceTest extends BaseUnitTest{
    private MotherReportingService service;
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllMothers allMothers;
    private MotherReportingService motherReportingService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        motherReportingService = new MotherReportingService(reportingService, allMothers);
        mockCurrentDate(new LocalDate(2012, 1, 1));
    }

    @Test
    public void shouldReportANCRegistrationWhichHasBeenDoneWithinTwelveWeeksOfLMP() throws Exception {
        assertThatIndicatorIsSetBasedOnLMP("2011-10-11", "ANC<12");
        assertThatIndicatorIsSetBasedOnLMP("2011-10-10", "ANC<12");
    }

    @Test
    public void shouldReportANCRegistrationWhichHasBeenDoneAfterExactlyTwelveWeeksAfterLMPAsLessThanTwelve() throws Exception {
        assertThatIndicatorIsSetBasedOnLMP("2011-10-09", "ANC<12");
    }

    @Test
    public void shouldReportANCRegistrationWhichHasBeenDoneAfterTwelveWeeksAfterLMPAsGreaterThanTwelve() throws Exception {
        assertThatIndicatorIsSetBasedOnLMP("2011-10-08", "ANC>12");
        assertThatIndicatorIsSetBasedOnLMP("2011-10-07", "ANC>12");
        assertThatIndicatorIsSetBasedOnLMP("2011-10-01", "ANC>12");
    }

    @Test
    public void shouldReportCloseANCCaseIfReasonIsDeath() throws Exception {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "Case X");
        reportData.put("closeReason", "death");
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("CASE-1", "TC 1", "Theresa").withAnm("ANM X", "12345").withLocation("bherya", "Sub Center", "PHC X"));

        motherReportingService.closeANC(reportData);

        ReportingData data = ReportingData.serviceProvidedData("ANM X", "TC 1", "MORT_M", "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        verify(reportingService).sendReportData(data);
    }

    @Test
    public void shouldNotReportCloseANCCaseIfReasonIsNotDeath() throws Exception {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "Case X");
        reportData.put("closeReason", "delivery");

        motherReportingService.closeANC(reportData);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldNotReportCloseANCCaseIfMotherIsNotFound() throws Exception {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "Case X");
        reportData.put("closeReason", "death");

        when(allMothers.findByCaseId("Case X")).thenReturn(null);
        motherReportingService.closeANC(reportData);

        verifyZeroInteractions(reportingService);
    }

    private void assertThatIndicatorIsSetBasedOnLMP(String lmp, String indicator) {
        SafeMap reportData = setUpReportData(lmp);

        ReportingService fakeReportingService = mock(ReportingService.class);
        MotherReportingService motherReportingService = new MotherReportingService(fakeReportingService, allMothers);

        motherReportingService.registerANC(reportData);

        ReportingData data = ReportingData.serviceProvidedData("ANM X", "TC 1", indicator, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        verify(fakeReportingService).sendReportData(data);
    }

    private SafeMap setUpReportData(String lmp) {
        SafeMap reportData = new SafeMap();
        reportData.put("anmIdentifier", "ANM X");
        reportData.put("thaayiCardNumber", "TC 1");
        reportData.put("lmp", lmp);
        reportData.put("village", "bherya");
        reportData.put("subCenter", "Sub Center");
        reportData.put("phc", "PHC X");
        return reportData;
    }
}
