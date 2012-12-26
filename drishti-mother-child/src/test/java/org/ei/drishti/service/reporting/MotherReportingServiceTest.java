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
import org.mockito.internal.verification.Times;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.Map;

import static org.ei.drishti.common.domain.Indicator.*;
import static org.ei.drishti.common.domain.ReportingData.anmReportData;
import static org.ei.drishti.common.domain.ReportingData.serviceProvidedData;
import static org.ei.drishti.util.EasyMap.create;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotherReportingServiceTest extends BaseUnitTest {
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllMothers allMothers;

    private MotherReportingService service;

    private final Mother MOTHER = new Mother("CASE-1", "EC-CASE-1", "TC 1", "Theresa")
            .withAnm("ANM X", "12345")
            .withLocation("bherya", "Sub Center", "PHC X");

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new MotherReportingService(reportingService, allMothers);
    }

    @Test
    public void shouldReportANCRegistrationWhichHasBeenDoneWithinTwelveWeeksOfLMP() throws Exception {
        assertThatIndicatorIsSetBasedOnLMP("2011-10-11", ANC_BEFORE_12_WEEKS);
        assertThatIndicatorIsSetBasedOnLMP("2011-10-11", ANC);
    }

    @Test
    public void shouldReportANCRegistrationWhichHasBeenDoneAfterExactlyTwelveWeeksAfterLMPAsLessThanTwelve() throws Exception {
        assertThatIndicatorIsSetBasedOnLMP("2011-10-09", ANC_BEFORE_12_WEEKS);
        assertThatIndicatorIsSetBasedOnLMP("2011-10-09", ANC);
    }

    @Test
    public void shouldNotReportANCRegistrationWhichHasBeenDoneAfterTwelveWeeksLMPAsEarlyRegistration() throws Exception {
        SafeMap reportData = new SafeMap();
        reportData.put("lmp", "2011-10-08");
        reportData.put("caseId", "CASE-1");
        reportData.put("registrationDate", "2012-01-01");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.registerANC(reportData);

        verify(reportingService, new Times(0)).sendReportData(serviceProvided(ANC_BEFORE_12_WEEKS, "2012-01-01"));
        verify(reportingService, new Times(0)).sendReportData(anmReport(ANC_BEFORE_12_WEEKS, "2012-01-01"));
    }

    @Test
    public void shouldReportCloseANCCaseIfReasonIsDeathAlongWithMTP() throws Exception {
        mockCurrentDate(new LocalDate(2012, 1, 1));
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("closeReason", "death_of_woman");
        reportData.put("mtpTime", "less_12wks");
        reportData.put("dateInduced", "2012-12-12");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.closeANC(reportData);

        verify(reportingService).sendReportData(serviceProvided(MOTHER_MORTALITY, "2012-01-01"));
        verify(reportingService).sendReportData(anmReport(MOTHER_MORTALITY, "2012-01-01"));
        verify(reportingService).sendReportData(serviceProvided(MTP_LESS_THAN_12_WEEKS, "2012-12-12"));
        verify(reportingService).sendReportData(anmReport(MTP_LESS_THAN_12_WEEKS, "2012-12-12"));
    }

    @Test
    public void shouldReportMTPIndicatorBasedOnMTPTime() throws Exception {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("mtpTime", "greater_12wks");
        reportData.put("closeReason", "delivery");
        reportData.put("dateInduced", "2012-12-12");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.closeANC(reportData);

        verify(reportingService).sendReportData(serviceProvided(MTP_GREATER_THAN_12_WEEKS, "2012-12-12"));
        verify(reportingService).sendReportData(anmReport(MTP_GREATER_THAN_12_WEEKS, "2012-12-12"));
    }

    @Test
    public void shouldNotReportIfReasonIsNotDeath() throws Exception {
        mockCurrentDate(new LocalDate(2012, 1, 1));
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("closeReason", "delivery");
        reportData.put("mtpTime", "greater_12wks");
        reportData.put("dateInduced", "2012-12-12");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.closeANC(reportData);

        verify(reportingService, times(0)).sendReportData(serviceProvided(MOTHER_MORTALITY, "2012-01-01"));
        verify(reportingService, times(0)).sendReportData(anmReport(MOTHER_MORTALITY, "2012-01-01"));
    }

    @Test
    public void shouldReportIfReasonIsSpontaneousAbortion() throws Exception {
        mockCurrentDate(new LocalDate(2012, 1, 1));
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("mtpTime", "");
        reportData.put("closeReason", "spontaneous_abortion");
        reportData.put("dateSpontaneous", "2012-12-12");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.closeANC(reportData);

        verify(reportingService).sendReportData(serviceProvided(SPONTANEOUS_ABORTION, "2012-12-12"));
        verify(reportingService).sendReportData(anmReport(SPONTANEOUS_ABORTION, "2012-12-12"));
    }

    @Test
    public void shouldReportTTProvidedIfTTVisitHasHappened() {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("visitDate", "2012-01-23");
        reportData.put("ttShotProvided", "true");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.ancHasBeenProvided(reportData);

        verify(reportingService).sendReportData(serviceProvided(TT, "2012-01-23"));
        verify(reportingService).sendReportData(anmReport(TT, "2012-01-23"));
    }

    @Test
    public void shouldNotReportTTProvidedIfTTVisitHasNotHappened() {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("visitDate", "2012-01-23");
        reportData.put("ttShotProvided", "false");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.ancHasBeenProvided(reportData);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldReportDeliveryWhenDeliveryOutcomeIsUpdatedWithOutcome() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        Map<String, String> reportData = create("motherCaseId", "CASE-1").put("pregnancyOutcome", "live_birth").put("dateOfDelivery", "2012-01-01").map();
        service.updatePregnancyOutcome(new SafeMap(reportData));

        verify(reportingService).sendReportData(serviceProvided(DELIVERY, "2012-01-01"));
        verify(reportingService).sendReportData(anmReport(DELIVERY, "2012-01-01"));
    }

    @Test
    public void shouldReportLiveBirthWhenDeliveryOutcomeIsUpdatedWithOutcomeAsLiveBirth() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        Map<String, String> reportData = create("motherCaseId", "CASE-1").put("pregnancyOutcome", "live_birth").put("dateOfDelivery", "2012-01-01").map();
        service.updatePregnancyOutcome(new SafeMap(reportData));

        verify(reportingService).sendReportData(serviceProvided(LIVE_BIRTH, "2012-01-01"));
        verify(reportingService).sendReportData(anmReport(LIVE_BIRTH, "2012-01-01"));
    }

    @Test
    public void shouldReportStillBirthWhenDeliveryOutcomeIsUpdatedWithOutcomeAsStillBirth() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        Map<String, String> reportData = create("motherCaseId", "CASE-1").put("anmIdentifier", "ANM 1").put("pregnancyOutcome", "still_birth").put("dateOfDelivery", "2012-01-01").map();
        service.updatePregnancyOutcome(new SafeMap(reportData));

        verify(reportingService).sendReportData(serviceProvided(STILL_BIRTH, "2012-01-01"));
        verify(reportingService).sendReportData(anmReport(STILL_BIRTH, "2012-01-01"));
    }

    private void assertThatIndicatorIsSetBasedOnLMP(String lmp, Indicator indicator) {
        SafeMap reportData = new SafeMap();
        reportData.put("lmp", lmp);
        reportData.put("caseId", "CASE-1");
        reportData.put("registrationDate", "2012-01-01");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        ReportingService fakeReportingService = mock(ReportingService.class);
        MotherReportingService motherReportingService = new MotherReportingService(fakeReportingService, allMothers);

        motherReportingService.registerANC(reportData);

        verify(fakeReportingService).sendReportData(serviceProvided(indicator, "2012-01-01"));
        verify(fakeReportingService).sendReportData(anmReport(indicator, "2012-01-01"));
    }

    private ReportingData anmReport(Indicator indicator, String date) {
        return anmReportData("ANM X", "CASE-1", indicator, date);
    }

    private ReportingData serviceProvided(Indicator indicator, String date) {
        return serviceProvidedData("ANM X", "TC 1", indicator, date, new Location("bherya", "Sub Center", "PHC X"));
    }
}
