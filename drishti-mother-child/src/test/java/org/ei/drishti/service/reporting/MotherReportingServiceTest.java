package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.EasyMap;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.Map;

import static org.ei.drishti.common.domain.Indicator.*;
import static org.ei.drishti.util.EasyMap.create;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotherReportingServiceTest extends BaseUnitTest {
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllEligibleCouples allEligibleCouples;


    private MotherReportingService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new MotherReportingService(reportingService, allMothers, allEligibleCouples);
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
        reportData.put("caseId", "CASE-1");
        reportData.put("closeReason", "death_of_woman");
        when(allMothers.findByCaseId("CASE-1")).thenReturn(new Mother("CASE-1", "EC-CASE-1", "TC 1", "Theresa")
                .withAnm("ANM X", "12345")
                .withLocation("bherya", "Sub Center", "PHC X"));

        service.closeANC(reportData);

        ReportingData serviceProvided = ReportingData.serviceProvidedData("ANM X", "TC 1", MOTHER_MORTALITY, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        verify(reportingService).sendReportData(serviceProvided);
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE-1", MOTHER_MORTALITY, "2012-01-01");
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

    @Test
    public void shouldReportTTProvidedIfTTVisitHasHappened() {
        Mother mother = new Mother("CASE-X", "ECCaseId X", "TC 1", "motherName");
        EligibleCouple ec = new EligibleCouple("ECCaseId X", "ecNumber").withLocation("bherya", "Sub Center", "PHC X");
        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("10");

        when(allMothers.findByCaseId("CASE-X")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ECCaseId X")).thenReturn(ec);
        service.ttVisitHasHappened(ancInformation);

        ReportingData serviceProvided = ReportingData.serviceProvidedData("ANM 1", "TC 1", TT, "2012-01-23", new Location("bherya", "Sub Center", "PHC X"));
        verify(reportingService).sendReportData(serviceProvided);
        ReportingData anmReportData = ReportingData.anmReportData("ANM 1", "CASE-X", TT, "2012-01-23");
        verify(reportingService).sendReportData(anmReportData);
    }

    @Test
    public void shouldNotReportTTProvidedIfTTVisitHasHappenedAndMotherIsNotFound() {
        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("10");
        when(allMothers.findByCaseId("CASE-X")).thenReturn(null);

        service.ttVisitHasHappened(ancInformation);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldNotReportTTProvidedIfTTVisitHasHappenedAndECIsNotFound() {
        Mother mother = new Mother("CASE-X", "ECCaseId X", "TC 1", "motherName");
        AnteNatalCareInformation ancInformation = new AnteNatalCareInformation("CASE-X", "ANM 1", 0, "2012-01-23").withNumberOfIFATabletsProvided("10");
        when(allMothers.findByCaseId("CASE-X")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ECCaseId X")).thenReturn(null);

        service.ttVisitHasHappened(ancInformation);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldReportLiveBirthWhenDeliveryOutcomeIsUpdatedWithOutcomeAsLiveBirth() {
        Mother mother = new Mother("CASE-X", "ECCaseId X", "TC 1", "motherName");
        EligibleCouple ec = new EligibleCouple("ECCaseId X", "ecNumber").withLocation("bherya", "Sub Center", "PHC X");
        when(allMothers.findByCaseId("CASE-X")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ECCaseId X")).thenReturn(ec);

        Map<String,String> reportData = create("motherCaseId", "CASE-X").put("anmIdentifier", "ANM 1").put("pregnancyOutcome", "live_birth").put("dateOfDelivery", "2012-01-01").map();
        service.updatePregnancyOutcome(reportData);

        ReportingData serviceProvided = ReportingData.serviceProvidedData("ANM 1", "TC 1", LIVE_BIRTH, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        verify(reportingService).sendReportData(serviceProvided);
        ReportingData anmReportData = ReportingData.anmReportData("ANM 1", "CASE-X", LIVE_BIRTH, "2012-01-01");
        verify(reportingService).sendReportData(anmReportData);
    }

    @Test
    public void shouldNotReportLiveBirthWhenDeliveryOutcomeIsUpdatedAndECIsNotFound() {
        Mother mother = new Mother("CASE-X", "ECCaseId X", "TC 1", "motherName");
        when(allMothers.findByCaseId("CASE-X")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ECCaseId X")).thenReturn(null);

        Map<String,String> reportData = create("motherCaseId", "CASE-X").put("anmIdentifier", "ANM 1").put("pregnancyOutcome", "live_birth").put("dateOfDelivery", "2012-01-01").map();
        service.updatePregnancyOutcome(reportData);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldNotReportLiveBirthWhenDeliveryOutcomeIsUpdatedAndMotherIsNotFound() {
        when(allMothers.findByCaseId("CASE-X")).thenReturn(null);

        Map<String,String> reportData = create("motherCaseId", "CASE-X").put("anmIdentifier", "ANM 1").put("pregnancyOutcome", "live_birth").put("dateOfDelivery", "2012-01-01").map();
        service.updatePregnancyOutcome(reportData);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldReportStillBirthWhenDeliveryOutcomeIsUpdatedWithOutcomeAsStillBirth() {
        Mother mother = new Mother("CASE-X", "ECCaseId X", "TC 1", "motherName");
        EligibleCouple ec = new EligibleCouple("ECCaseId X", "ecNumber").withLocation("bherya", "Sub Center", "PHC X");
        when(allMothers.findByCaseId("CASE-X")).thenReturn(mother);
        when(allEligibleCouples.findByCaseId("ECCaseId X")).thenReturn(ec);

        Map<String,String> reportData = create("motherCaseId", "CASE-X").put("anmIdentifier", "ANM 1").put("pregnancyOutcome", "still_birth").put("dateOfDelivery", "2012-01-01").map();
        service.updatePregnancyOutcome(reportData);

        ReportingData serviceProvided = ReportingData.serviceProvidedData("ANM 1", "TC 1", STILL_BIRTH, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        verify(reportingService).sendReportData(serviceProvided);
        ReportingData anmReportData = ReportingData.anmReportData("ANM 1", "CASE-X", STILL_BIRTH, "2012-01-01");
        verify(reportingService).sendReportData(anmReportData);
    }

    private void assertThatIndicatorIsSetBasedOnLMP(String lmp, Indicator indicator) {
        SafeMap reportData = setUpReportData(lmp);

        ReportingService fakeReportingService = mock(ReportingService.class);
        MotherReportingService motherReportingService = new MotherReportingService(fakeReportingService, allMothers, allEligibleCouples);

        motherReportingService.registerANC(reportData, "bherya", "Sub Center");

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", indicator, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "Case Id", indicator, "2012-01-01");
        verify(fakeReportingService).sendReportData(serviceProvidedData);
        verify(fakeReportingService).sendReportData(anmReportData);
    }

    private SafeMap setUpReportData(String lmp) {
        SafeMap reportData = new SafeMap();
        reportData.put("anmIdentifier", "ANM X");
        reportData.put("thaayiCardNumber", "TC 1");
        reportData.put("caseId", "Case Id");
        reportData.put("lmp", lmp);
        reportData.put("village", "bherya");
        reportData.put("subCenter", "Sub Center");
        reportData.put("phc", "PHC X");
        return reportData;
    }
}
