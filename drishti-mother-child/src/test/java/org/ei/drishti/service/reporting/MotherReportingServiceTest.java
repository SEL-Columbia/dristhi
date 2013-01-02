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

import java.util.Map;

import static org.ei.drishti.common.domain.Indicator.*;
import static org.ei.drishti.common.domain.ReportingData.anmReportData;
import static org.ei.drishti.common.domain.ReportingData.serviceProvidedData;
import static org.ei.drishti.util.EasyMap.create;
import static org.joda.time.LocalDate.parse;
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
            .withLocation("bherya", "Sub Center", "PHC X")
            .withLMP(parse("2012-01-01"));

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

        verifyNoReportingCalls(ANC_BEFORE_12_WEEKS, "2012-01-01");
    }

    @Test
    public void shouldReportMTPIndicatorBasedOnMTPTime() throws Exception {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.closeANC(reportDataForANCClose("greater_12wks", "spontaneous_abortion", null));

        verifyBothReportingCalls(MTP_GREATER_THAN_12_WEEKS, "2012-12-12");
    }

    @Test
    public void shouldReportMaternalDeath() throws Exception {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.closeANC(reportDataForANCClose("", "death_of_woman", "yes"));

        verifyBothReportingCalls(MMA, "2012-12-12");
        verifyBothReportingCalls(MOTHER_MORTALITY, "2012-12-12");
    }

    @Test
    public void shouldNotReportMaternalDeathIfItIsNot() throws Exception {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.closeANC(reportDataForANCClose("", "death_of_woman", "no"));

        verifyNoReportingCalls(MMA, "2012-12-12");
    }

    @Test
    public void shouldNotReportIfReasonIsNotDeath() throws Exception {
        mockCurrentDate(new LocalDate(2012, 1, 1));
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.closeANC(reportDataForANCClose("greater_12wks", null, null));

        verifyNoReportingCalls(MOTHER_MORTALITY, "2012-01-01");
    }

    @Test
    public void shouldReportIfReasonIsSpontaneousAbortion() throws Exception {
        mockCurrentDate(new LocalDate(2012, 1, 1));
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.closeANC(reportDataForANCClose("", "spontaneous_abortion", null));

        verifyBothReportingCalls(SPONTANEOUS_ABORTION, "2012-12-12");
    }

    @Test
    public void shouldReportTTProvidedIfTTVisitHasHappened() {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("visitDate", "2012-01-23");
        reportData.put("visitNumber", "4");
        reportData.put("ttDose", "TT1");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.ancHasBeenProvided(reportData);

        verifyBothReportingCalls(TT, "2012-01-23");
    }

    @Test
    public void shouldNotReportTTProvidedIfTTVisitHasNotHappened() {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("visitDate", "2012-01-23");
        reportData.put("visitNumber", "4");
        reportData.put("ttDose", "");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.ancHasBeenProvided(reportData);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldReportANC4ProvidedWhenANCCareIsProvidedAfter36Weeks() {
        String visitDate = parse("2012-01-01").plusWeeks(36).toString();
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("visitDate", visitDate);
        reportData.put("ttDose", "");
        reportData.put("visitNumber", "4");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.ancHasBeenProvided(reportData);

        verifyBothReportingCalls(ANC4, visitDate);
    }

    @Test
    public void shouldNotReportANC4ProvidedWhenANCVisitIsNotFourthVisit() {
        String visitDate = parse("2012-01-01").plusWeeks(36).toString();
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("visitDate", visitDate);
        reportData.put("ttDose", "");
        reportData.put("visitNumber", "3");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.ancHasBeenProvided(reportData);

        verifyNoReportingCalls(ANC4, visitDate);
    }

    @Test
    public void shouldNotReportANC4ProvidedWhenANCCareIsProvidedBefore36Weeks() {
        String visitDate = parse("2012-01-01").plusWeeks(36).minusDays(1).toString();
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("visitDate", visitDate);
        reportData.put("ttDose", "");
        reportData.put("visitNumber", "4");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        service.ancHasBeenProvided(reportData);

        verifyNoReportingCalls(ANC4, visitDate);
    }

    @Test
    public void shouldReportDeliveryWhenDeliveryOutcomeIsUpdatedWithOutcome() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        Map<String, String> reportData = create("motherCaseId", "CASE-1")
                .put("womanSurvived", "")
                .put("motherSurvived", "yes")
                .put("pregnancyOutcome", "live_birth")
                .put("dateOfDelivery", "2012-01-01")
                .put("placeOfDelivery", "home").map();
        service.updatePregnancyOutcome(new SafeMap(reportData));

        verifyBothReportingCalls(DELIVERY, "2012-01-01");
    }

    @Test
    public void shouldReportInstitutionalDeliveryWhenPlaceOfDeliveryIsNotHome() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        Map<String, String> reportData = create("motherCaseId", "CASE-1")
                .put("womanSurvived", "")
                .put("motherSurvived", "yes")
                .put("pregnancyOutcome", "live_birth")
                .put("dateOfDelivery", "2012-01-01")
                .put("placeOfDelivery", "phc").map();
        service.updatePregnancyOutcome(new SafeMap(reportData));

        verifyBothReportingCalls(INSTITUTIONAL_DELIVERY, "2012-01-01");
    }

    @Test
    public void shouldNotReportInstitutionalDeliveryWhenPlaceOfDeliveryIsHome() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        Map<String, String> reportData = create("motherCaseId", "CASE-1")
                .put("womanSurvived", "")
                .put("motherSurvived", "yes")
                .put("pregnancyOutcome", "live_birth")
                .put("dateOfDelivery", "2012-01-01")
                .put("placeOfDelivery", "home").map();
        service.updatePregnancyOutcome(new SafeMap(reportData));

        verifyNoReportingCalls(INSTITUTIONAL_DELIVERY, "2012-01-01");
    }

    @Test
    public void shouldReportLiveBirthWhenDeliveryOutcomeIsUpdatedWithOutcomeAsLiveBirth() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        Map<String, String> reportData = create("motherCaseId", "CASE-1")
                .put("womanSurvived", "")
                .put("motherSurvived", "yes")
                .put("pregnancyOutcome", "live_birth")
                .put("dateOfDelivery", "2012-01-01")
                .put("placeOfDelivery", "phc").map();
        service.updatePregnancyOutcome(new SafeMap(reportData));

        verifyBothReportingCalls(LIVE_BIRTH, "2012-01-01");
    }

    @Test
    public void shouldReportStillBirthWhenDeliveryOutcomeIsUpdatedWithOutcomeAsStillBirth() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        Map<String, String> reportData = create("motherCaseId", "CASE-1")
                .put("womanSurvived", "")
                .put("motherSurvived", "yes")
                .put("anmIdentifier", "ANM 1")
                .put("pregnancyOutcome", "still_birth")
                .put("dateOfDelivery", "2012-01-01")
                .put("placeOfDelivery", "phc").map();
        service.updatePregnancyOutcome(new SafeMap(reportData));

        verifyBothReportingCalls(STILL_BIRTH, "2012-01-01");
    }

    @Test
    public void shouldReportMotherDeathDuringPregnancyOutcome() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        Map<String, String> reportData = create("motherCaseId", "CASE-1")
                .put("anmIdentifier", "ANM 1")
                .put("motherSurvived", "no")
                .put("womanSurvived", "")
                .put("pregnancyOutcome", "live_birth")
                .put("placeOfDelivery", "phc")
                .put("dateOfDelivery", "2012-01-01").map();
        service.updatePregnancyOutcome(new SafeMap(reportData));

        verifyBothReportingCalls(MMD, "2012-01-01");
        verifyBothReportingCalls(MOTHER_MORTALITY, "2012-01-01");
    }

    @Test
    public void shouldReportWomanDeathDuringPregnancyOutcome() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER);

        Map<String, String> reportData = create("motherCaseId", "CASE-1")
                .put("anmIdentifier", "ANM 1")
                .put("motherSurvived", "")
                .put("womanSurvived", "no")
                .put("pregnancyOutcome", "live_birth")
                .put("placeOfDelivery", "phc")
                .put("dateOfDelivery", "2012-01-01").map();
        service.updatePregnancyOutcome(new SafeMap(reportData));

        verifyBothReportingCalls(MMD, "2012-01-01");
        verifyBothReportingCalls(MOTHER_MORTALITY, "2012-01-01");
    }

    @Test
    public void shouldReportMotherDeathDuringPNCClose() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER.withDeliveryOutCome("2012-01-01"));

        Map<String, String> reportData = create("caseId", "CASE-1")
                .put("closeReason", "death_of_mother")
                .put("isMaternalDeath", "yes")
                .put("diedOn", "2012-02-01")
                .map();
        service.closePNC(new SafeMap(reportData));

        verifyBothReportingCalls(MMP, "2012-02-01");
        verifyBothReportingCalls(MOTHER_MORTALITY, "2012-02-01");
    }

    @Test
    public void shouldNotReportMotherDeathDuringPNCCloseIfCloseReasonIsNotDeath() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER.withDeliveryOutCome("2012-01-01"));

        Map<String, String> reportData = create("caseId", "CASE-1")
                .put("closeReason", "")
                .put("isMaternalDeath", "")
                .put("diedOn", "2012-02-01")
                .map();
        service.closePNC(new SafeMap(reportData));

        verifyNoReportingCalls(MMP, "2012-02-01");
    }

    @Test
    public void shouldNotReportMotherDeathDuringPNCCloseIfNotMaternalDeath() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER.withDeliveryOutCome("2012-01-01"));

        Map<String, String> reportData = create("caseId", "CASE-1")
                .put("closeReason", "death_of_mother")
                .put("isMaternalDeath", "")
                .put("diedOn", "2012-02-01")
                .map();
        service.closePNC(new SafeMap(reportData));

        verifyNoReportingCalls(MMP, "2012-02-01");
    }

    @Test
    public void shouldNotReportMotherDeathDuringPNCCloseIfNotWithin42DaysOfDeliveryOutcome() {
        when(allMothers.findByCaseId("CASE-1")).thenReturn(MOTHER.withDeliveryOutCome("2012-01-01"));

        Map<String, String> reportData = create("caseId", "CASE-1")
                .put("closeReason", "death_of_mother")
                .put("isMaternalDeath", "yes")
                .put("diedOn", "2012-02-12")
                .map();
        service.closePNC(new SafeMap(reportData));

        verifyNoReportingCalls(MMP, "2012-02-12");
    }

    private void verifyBothReportingCalls(Indicator indicator, String date) {
        verify(reportingService).sendReportData(serviceProvided(indicator, date));
        verify(reportingService).sendReportData(anmReport(indicator, date));
    }

    private void verifyNoReportingCalls(Indicator indicator, String date) {
        verify(reportingService, times(0)).sendReportData(serviceProvided(indicator, date));
        verify(reportingService, times(0)).sendReportData(anmReport(indicator, date));
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

    private SafeMap reportDataForANCClose(String mtpTime, String closeReason, String isMaternalDeath) {
        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE-1");
        reportData.put("mtpTime", mtpTime);
        reportData.put("closeReason", closeReason);
        reportData.put("dateSpontaneous", "2012-12-12");
        reportData.put("dateInduced", "2012-12-12");
        reportData.put("isMaternalDeath", isMaternalDeath);
        reportData.put("diedOn", "2012-12-12");
        return reportData;
    }
}
