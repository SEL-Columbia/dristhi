package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.domain.Indicator.*;
import static org.ei.drishti.common.domain.Indicator.DPT3_OR_OPV3;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildReportingServiceTest {
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllChildren allChildren;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllEligibleCouples allEligibleCouples;

    private ChildReportingService service;

    private static final Child CHILD = new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "female")
            .withDateOfBirth("2012-01-01")
            .withLocation("bherya", "Sub Center", "PHC X")
            .withAnm("ANM X")
            .withThayiCard("TC 1");

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ChildReportingService(reportingService, allChildren, allMothers, allEligibleCouples);
    }

    @Test
    public void shouldGetRidOfSequenceNumberFormImmunizationReportIndicator() throws Exception {
        assertIndicatorBasedOnImmunization("bcg", BCG);

        assertIndicatorBasedOnImmunization("dptbooster_1", DPT, DPT_BOOSTER_OR_OPV_BOOSTER);
        assertIndicatorBasedOnImmunization("dptbooster_2", DPT_BOOSTER2);

        assertIndicatorBasedOnImmunization("hepb_0", HEP);
        assertIndicatorBasedOnImmunization("hepb_1", HEP);
        assertIndicatorBasedOnImmunization("hepb_2", HEP);
        assertIndicatorBasedOnImmunization("hepb_3", HEP);

        assertIndicatorBasedOnImmunization("opv_0", OPV);
        assertIndicatorBasedOnImmunization("opv_1", OPV);
        assertIndicatorBasedOnImmunization("opv_2", OPV);
        assertIndicatorBasedOnImmunization("opv_3", OPV, DPT3_OR_OPV3);
        assertIndicatorBasedOnImmunization("opvbooster", OPV, DPT_BOOSTER_OR_OPV_BOOSTER);

        assertIndicatorBasedOnImmunization("measles", MEASLES);

        assertIndicatorBasedOnImmunization("pentavalent_1", PENT1);
        assertIndicatorBasedOnImmunization("pentavalent_2", PENT2);
        assertIndicatorBasedOnImmunization("pentavalent_3", PENT3);

        assertIndicatorBasedOnImmunization("mmr", MMR);
        assertIndicatorBasedOnImmunization("je", JE);
    }

    @Test
    public void shouldMakeAReportingCallForEachNewlyProvidedImmunization() throws Exception {
        SafeMap reportingData = reportDataForImmunization("opv_1 bcg measles", "");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "female")
                .withLocation("bherya", "Sub Center", "PHC X")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.immunizationProvided(reportingData, asList("opv_1"));

        verifyBothReportingCalls(BCG, "2012-01-01");
        verifyBothReportingCalls(MEASLES, "2012-01-01");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportFirstVitaminDoseDuringImmunizationProvided() throws Exception {
        SafeMap reportingData = reportDataForImmunization("", "1");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "female")
                .withLocation("bherya", "Sub Center", "PHC X")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.vitaminAProvided(reportingData);

        verifyBothReportingCalls(VIT_A_1, "2012-01-01");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportSecondVitaminDoseDuringImmunizationProvided() throws Exception {
        SafeMap reportingData = reportDataForImmunization("", "2");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "female")
                .withLocation("bherya", "Sub Center", "PHC X")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.vitaminAProvided(reportingData);

        verifyBothReportingCalls(VIT_A_2, "2012-01-01");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldNotSendChildReportingDataWhenWrongImmunizationIsProvided() throws Exception {
        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationDate", "2012-01-01");
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.immunizationProvided(reportDataForImmunization("NON_EXISTENT_IMMUNIZATION bcg", ""), new ArrayList<String>());

        verifyBothReportingCalls(BCG, "2012-01-01");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportChildImmunizationDataWhenChildIsRegistered() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap();
        reportData.put("id", "CASE X");
        reportData.put("didBreastfeedingStart", "");
        service.registerChild(reportData);

        verifyBothReportingCalls(OPV, "2012-01-01");
    }

    @Test
    public void shouldReportLowWeightDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "boo", "2.2", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap();
        reportData.put("id", "CASE X");
        reportData.put("didBreastfeedingStart", "");
        service.registerChild(reportData);

        verifyBothReportingCalls(LBW, "2012-01-01");
    }

    @Test
    public void shouldReportIfBreastFeedingInitiatedDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "boo", "2.2", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap();
        reportData.put("id", "CASE X");
        reportData.put("didBreastfeedingStart", "yes");
        service.registerChild(reportData);

        verifyBothReportingCalls(BF_POST_BIRTH, "2012-01-01");
    }

    @Test
    public void shouldReportIfWeightIsMeasuredDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "boo", "2.2", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap();
        reportData.put("id", "CASE X");
        reportData.put("didBreastfeedingStart", "");
        service.registerChild(reportData);

        verifyBothReportingCalls(WEIGHED_AT_BIRTH, "2012-01-01");
    }

    @Test
    public void shouldNotReportIfWeightIsNotMeasuredDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "boo", "", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap();
        reportData.put("id", "CASE X");
        reportData.put("didBreastfeedingStart", "");
        service.registerChild(reportData);

        verifyNoReportingCalls(WEIGHED_AT_BIRTH, "2012-01-01");
    }

    @Test
    public void shouldNotReportBreastFeedingIfNotInitiatedDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "boo", "2.2", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap();
        reportData.put("id", "CASE X");
        reportData.put("didBreastfeedingStart", "");
        service.registerChild(reportData);

        verifyNoReportingCalls(BF_POST_BIRTH, "2012-01-01");
    }

    @Test
    public void shouldNotReportNormalWeight() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "boo", "2.5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap();
        reportData.put("id", "CASE X");
        reportData.put("didBreastfeedingStart", "");
        service.registerChild(reportData);

        verifyNoReportingCalls(LBW, "2012-01-01");
    }

    @Test
    public void shouldNotReportInvalidWeightValue() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "boo", "---", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap();
        reportData.put("id", "CASE X");
        reportData.put("didBreastfeedingStart", "");
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

    private void assertIndicatorBasedOnImmunization(String immunizationProvided, Indicator... expectedIndicators) {
        ReportingService fakeReportingService = mock(ReportingService.class);
        ChildReportingService childReportingService = new ChildReportingService(fakeReportingService, allChildren, allMothers, allEligibleCouples);
        SafeMap reportingData = reportDataForImmunization(immunizationProvided, "");
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        childReportingService.immunizationProvided(reportingData, new ArrayList<String>());

        for (Indicator expectedIndicator : expectedIndicators) {
            verify(fakeReportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", expectedIndicator, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
            verify(fakeReportingService).sendReportData(ReportingData.anmReportData("ANM X", "CASE X", expectedIndicator, "2012-01-01"));
        }
        verifyNoMoreInteractions(fakeReportingService);
    }

    private SafeMap reportDataForImmunization(String immunizationProvided, String vitaminADose) {
        SafeMap reportingData = new SafeMap();
        reportingData.put("id", "CASE X");
        reportingData.put("immunizationsGiven", immunizationProvided);
        reportingData.put("immunizationDate", "2012-01-01");
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
