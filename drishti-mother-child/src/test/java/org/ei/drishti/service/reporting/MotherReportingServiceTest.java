package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.testing.utils.BaseUnitTest;

import static org.ei.drishti.common.domain.Indicator.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotherReportingServiceTest extends BaseUnitTest{
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllMothers allMothers;

    private MotherReportingService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new MotherReportingService(reportingService, allMothers);
        mockCurrentDate(new LocalDate(2012, 1, 1));
    }

    @Test
    public void shouldReportANCRegistrationWhichHasBeenDoneWithinTwelveWeeksOfLMP() throws Exception {
        assertThatIndicatorIsSetBasedOnLMP("2011-10-11", ANC_BEFORE_12_WEEKS);
        assertThatIndicatorIsSetBasedOnLMP("2011-10-10", ANC_BEFORE_12_WEEKS);
    }

    @Test
    public void shouldReportANCRegistrationWhichHasBeenDoneAfterExactlyTwelveWeeksAfterLMPAsLessThanTwelve() throws Exception {
        assertThatIndicatorIsSetBasedOnLMP("2011-10-09", ANC_BEFORE_12_WEEKS);
    }

    @Test
    public void shouldReportANCRegistrationWhichHasBeenDoneAfterTwelveWeeksAfterLMPAsGreaterThanTwelve() throws Exception {
        assertThatIndicatorIsSetBasedOnLMP("2011-10-08", ANC_AFTER_12_WEEKS);
        assertThatIndicatorIsSetBasedOnLMP("2011-10-07", ANC_AFTER_12_WEEKS);
        assertThatIndicatorIsSetBasedOnLMP("2011-10-01", ANC_AFTER_12_WEEKS);
    }

    @Test
    public void shouldReportCloseANCCaseIfReasonIsDeath() throws Exception {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "Case X");
        reportData.put("closeReason", "death_of_woman");
        when(allMothers.findByCaseId("Case X")).thenReturn(new Mother("CASE-1", "EC-CASE-1", "TC 1", "Theresa")
                .withAnm("ANM X", "12345")
                .withLocation("bherya", "Sub Center", "PHC X"));

        service.closeANC(reportData);

        ReportingData serviceProvided = ReportingData.serviceProvidedData("ANM X", "TC 1", MOTHER_MORTALITY, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        verify(reportingService).sendReportData(serviceProvided);
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "TC 1", MOTHER_MORTALITY, "2012-01-01");
        verify(reportingService).sendReportData(anmReportData);
    }

    @Test
    public void shouldNotReportCloseANCCaseIfReasonIsNotDeath() throws Exception {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "Case X");
        reportData.put("closeReason", "delivery");

        service.closeANC(reportData);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldNotReportCloseANCCaseIfMotherIsNotFound() throws Exception {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "Case X");
        reportData.put("closeReason", "death");

        when(allMothers.findByCaseId("Case X")).thenReturn(null);
        service.closeANC(reportData);

        verifyZeroInteractions(reportingService);
    }

    private void assertThatIndicatorIsSetBasedOnLMP(String lmp, Indicator indicator) {
        SafeMap reportData = setUpReportData(lmp);

        ReportingService fakeReportingService = mock(ReportingService.class);
        MotherReportingService motherReportingService = new MotherReportingService(fakeReportingService, allMothers);

        motherReportingService.registerANC(reportData, "bherya", "Sub Center");

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", indicator, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "TC 1", indicator, "2012-01-01");
        verify(fakeReportingService).sendReportData(serviceProvidedData);
        verify(fakeReportingService).sendReportData(anmReportData);
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
