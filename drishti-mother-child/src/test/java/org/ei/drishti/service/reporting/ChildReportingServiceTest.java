package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.domain.Indicator.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildReportingServiceTest {
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllChildren allChildren;

    private ChildReportingService service;

    private static final Child CHILD = new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", new ArrayList<String>(), "female")
            .withDateOfBirth("2012-01-01")
            .withLocation("bherya", "Sub Center", "PHC X")
            .withAnm("ANM X");

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ChildReportingService(reportingService, allChildren);
    }

    @Test
    public void shouldGetRidOfSequenceNumberFormImmunizationReportIndicator() throws Exception {
        assertIndicatorBasedOnImmunization("bcg", BCG);

        assertIndicatorBasedOnImmunization("dpt_1", DPT1);
        assertIndicatorBasedOnImmunization("dpt_2", DPT2);
        assertIndicatorBasedOnImmunization("dpt_3", DPT3);
        assertIndicatorBasedOnImmunization("dptbooster_1", DPT);
        assertIndicatorBasedOnImmunization("dptbooster_2", DPT_BOOSTER2);

        assertIndicatorBasedOnImmunization("hepb_0", HEP);
        assertIndicatorBasedOnImmunization("hepb_1", HEP);
        assertIndicatorBasedOnImmunization("hepb_2", HEP);
        assertIndicatorBasedOnImmunization("hepb_3", HEP);

        assertIndicatorBasedOnImmunization("opv_0", OPV);
        assertIndicatorBasedOnImmunization("opv_1", OPV);
        assertIndicatorBasedOnImmunization("opv_2", OPV);
        assertIndicatorBasedOnImmunization("opv_3", OPV);
        assertIndicatorBasedOnImmunization("opvbooster", OPV);

        assertIndicatorBasedOnImmunization("measles", MEASLES);
        assertIndicatorBasedOnImmunization("measlesbooster", MEASLES);
    }

    @Test
    public void shouldMakeAReportingCallForEachNewlyProvidedImmunization() throws Exception {
        SafeMap reportingData = reportDataForImmunization("dpt_1 bcg measles", "");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", asList("dpt_1", "dpt_2", "bcg", "measles"), "female")
                .withLocation("bherya", "Sub Center", "PHC X")
                .withAnm("ANM X"));

        service.immunizationProvided(reportingData, asList("dpt_1"));

        verifyBothReportingCalls(BCG, "2012-01-01");
        verifyBothReportingCalls(MEASLES, "2012-01-01");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportFirstVitaminDoseDuringImmunizationProvided() throws Exception {
        SafeMap reportingData = reportDataForImmunization("", "1");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", asList("dpt_1", "dpt_2", "bcg", "measles"), "female")
                .withLocation("bherya", "Sub Center", "PHC X")
                .withAnm("ANM X"));

        service.immunizationProvided(reportingData, asList("dpt_1"));

        verifyBothReportingCalls(VIT_A_1, "2012-01-01");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportSecondVitaminDoseDuringImmunizationProvided() throws Exception {
        SafeMap reportingData = reportDataForImmunization("", "2");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", asList("dpt_1", "dpt_2", "bcg", "measles"), "female")
                .withLocation("bherya", "Sub Center", "PHC X")
                .withAnm("ANM X"));

        service.immunizationProvided(reportingData, asList("dpt_1"));

        verifyBothReportingCalls(VIT_A_2, "2012-01-01");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldNotSendChildReportingDataWhenWrongImmunizationIsProvided() throws Exception {
        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.immunizationProvided(reportDataForImmunization("NON_EXISTENT_IMMUNIZATION bcg", ""), new ArrayList<String>());

        verifyBothReportingCalls(BCG, "2012-01-01");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldSendChildImmunizationDataWhenChildIsRegistered() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", asList("opv_0"), "female")
                .withLocation("bherya", "Sub Center", "PHC X")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01"));

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "5");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        verifyBothReportingCalls(OPV, "2012-01-01");
    }

    @Test
    public void shouldReportLowWeightDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "2.2");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        verifyBothReportingCalls(LBW, "2012-01-01");
    }

    @Test
    public void shouldReportIfBreastFeedingInitiatedDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "2.2");
        reportData.put("bfPostBirth", "yes");
        service.registerChild(reportData);

        verifyBothReportingCalls(BF_POST_BIRTH, "2012-01-01");
    }

    @Test
    public void shouldReportIfWeightIsMeasuredDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "2.2");
        reportData.put("bfPostBirth", "yes");
        service.registerChild(reportData);

        verifyBothReportingCalls(WEIGHED_AT_BIRTH, "2012-01-01");
    }

    @Test
    public void shouldNotReportIfWeightIsNotMeasuredDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "---");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        verifyNoReportingCalls(WEIGHED_AT_BIRTH, "2012-01-01");
    }

    @Test
    public void shouldNotReportBreastFeedingIfNotInitiatedDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "2.2");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        verifyNoReportingCalls(BF_POST_BIRTH, "2012-01-01");
    }

    @Test
    public void shouldNotReportNormalWeight() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "2.5");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        verifyNoReportingCalls(LBW, "2012-01-01");
    }

    @Test
    public void shouldNotReportInvalidWeightValue() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        verifyNoReportingCalls(LBW, "2012-01-01");
    }

    @Test
    public void shouldReportCloseChildCaseWhenReasonIsDeath() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-03-05"));

        verifyBothReportingCalls(INFANT_MORTALITY, "2012-03-05");
    }

    @Test
    public void shouldReportEarlyNeonatalMortalityDeath() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-07"));

        verifyBothReportingCalls(ENM, "2012-01-07");
    }

    @Test
    public void shouldNotReportEarlyNeonatalMortalityDeathIfDateOfDeathIsAfterOneWeekOfBirth() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-08"));

        verifyNoReportingCalls(ENM, "2012-01-08");
    }

    @Test
    public void shouldReportNeonatalMortalityDeath() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-28"));

        verifyBothReportingCalls(NM, "2012-01-28");
    }

    @Test
    public void shouldNotReportNeonatalMortalityDeathIfDateOfDeathIsAfterOneWeekOfBirth() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-29"));

        verifyNoReportingCalls(NM, "2012-01-29");
    }

    @Test
    public void shouldReportLateMortalityWithin29DaysAnd1YearOfBirth() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-29"));
        service.closeChild(reportDataForCloseChild("death_of_child", "2012-12-31"));

        verifyBothReportingCalls(LNM, "2012-01-29");
        verifyBothReportingCalls(LNM, "2012-12-31");
    }

    @Test
    public void shouldNotReportLateMortalityIfNotWithin29DaysAnd1YearOfBirth() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-28"));
        service.closeChild(reportDataForCloseChild("death_of_child", "2013-01-01"));

        verifyNoReportingCalls(LNM, "2012-01-28");
        verifyNoReportingCalls(LNM, "2013-12-31");
    }

    @Test
    public void shouldReportChildMortalityWithin7DaysAnd1YearOfBirth() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-08"));
        service.closeChild(reportDataForCloseChild("death_of_child", "2012-12-31"));

        verifyNoReportingCalls(INFANT_MORTALITY, "2012-01-07");
        verifyNoReportingCalls(INFANT_MORTALITY, "2013-01-01");
    }

    @Test
    public void shouldReportChildMortalityWithin5YearOfBirth() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child", "2016-12-31"));

        verifyBothReportingCalls(CHILD_MORTALITY, "2016-12-31");
    }

    @Test
    public void shouldNotReportChildMortalityAfter5YearOfBirth() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child", "2017-01-01"));

        verifyNoReportingCalls(CHILD_MORTALITY, "2017-01-01");
    }

    @Test
    public void shouldNotReportCloseChildCaseWhenReasonIsNotDeath() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("child_over5", "2012-03-05"));

        verifyZeroInteractions(reportingService);
    }

    private void assertIndicatorBasedOnImmunization(String immunizationProvided, Indicator expectedIndicator) {
        ReportingService fakeReportingService = mock(ReportingService.class);
        ChildReportingService childReportingService = new ChildReportingService(fakeReportingService, allChildren);
        SafeMap reportingData = reportDataForImmunization(immunizationProvided, "");
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        childReportingService.immunizationProvided(reportingData, new ArrayList<String>());

        verify(fakeReportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", expectedIndicator, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verify(fakeReportingService).sendReportData(ReportingData.anmReportData("ANM X", "CASE X", expectedIndicator, "2012-01-01"));
        verifyNoMoreInteractions(fakeReportingService);
    }

    private SafeMap reportDataForImmunization(String immunizationProvided, String vitaminADose) {
        SafeMap reportingData = new SafeMap();
        reportingData.put("caseId", "CASE X");
        reportingData.put("immunizationsProvided", immunizationProvided);
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        reportingData.put("vitaminADose", vitaminADose);
        return reportingData;
    }

    private SafeMap reportDataForCloseChild(String closeReason, String closeDate) {
        SafeMap reportingData = new SafeMap();
        reportingData.put("caseId", "CASE X");
        reportingData.put("closeReason", closeReason);
        reportingData.put("diedOn", closeDate);
        return reportingData;
    }

    private void verifyBothReportingCalls(Indicator indicator, String date) {
        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", indicator, date, new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", indicator, date);
        verify(reportingService).sendReportData(serviceProvidedData);
        verify(reportingService).sendReportData(anmReportData);
    }

    private void verifyNoReportingCalls(Indicator indicator, String date) {
        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", indicator, date, new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", indicator, date);
        verify(reportingService, times(0)).sendReportData(serviceProvidedData);
        verify(reportingService, times(0)).sendReportData(anmReportData);
    }
}
