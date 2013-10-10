package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportDataUpdateRequest;
import org.ei.drishti.common.domain.ReportMonth;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.domain.*;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllInfantBalanceOnHandReportTokens;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.EasyMap;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.ANM_REPORT_DATA_TYPE;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.SERVICE_PROVIDER_TYPE;
import static org.ei.drishti.common.domain.Indicator.*;
import static org.joda.time.LocalDate.parse;
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
    @Mock
    private AllInfantBalanceOnHandReportTokens allInfantBalanceOnHandTokens;
    @Mock
    private ReportMonth reportMonth;

    private ChildReportingService service;

    private static final Child CHILD = new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "female")
            .withDateOfBirth("2012-01-01")
            .withLocation("bherya", "Sub Center", "PHC X")
            .withAnm("ANM X")
            .withThayiCard("TC 1");

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ChildReportingService(reportingService, allChildren, allMothers, allEligibleCouples, allInfantBalanceOnHandTokens, reportMonth);
    }

    @Test
    public void shouldGetRidOfSequenceNumberFormImmunizationReportIndicator() throws Exception {
        assertIndicatorBasedOnImmunization("bcg", BCG);

        assertIndicatorBasedOnImmunization("dptbooster_1", DPT, DPT_BOOSTER_OR_OPV_BOOSTER);
        assertIndicatorBasedOnImmunization("dptbooster_2", DPT_BOOSTER2);

        assertIndicatorBasedOnImmunization("opv_0", OPV);
        assertIndicatorBasedOnImmunization("opv_1", OPV);
        assertIndicatorBasedOnImmunization("opv_2", OPV);
        assertIndicatorBasedOnImmunization("opv_3", OPV, PENTAVALENT3_OR_OPV3);
        assertIndicatorBasedOnImmunization("opvbooster", OPV, DPT_BOOSTER_OR_OPV_BOOSTER);

        assertIndicatorBasedOnImmunization("measles", MEASLES);

        assertIndicatorBasedOnImmunization("pentavalent_1", PENT1);
        assertIndicatorBasedOnImmunization("pentavalent_2", PENT2);
        assertIndicatorBasedOnImmunization("pentavalent_3", PENT3, PENTAVALENT3_OR_OPV3);

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
    public void shouldReportFirstVitaminDoseDuringImmunizationProvidedForFemaleChild() throws Exception {
        SafeMap reportingData = reportDataForVitaminA("1", "2012-01-02");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "female")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.vitaminAProvided(reportingData);

        verifyBothReportingCalls(VIT_A_1, "2012-01-02");
        verifyBothReportingCalls(VIT_A_1_FOR_FEMALE_CHILD, "2012-01-02");
        verifyBothReportingCalls(VIT_A_FOR_FEMALE, "2012-01-02");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportSecondVitaminDoseDuringImmunizationProvidedForFemaleChild() throws Exception {
        SafeMap reportingData = reportDataForVitaminA("2", "2012-01-02");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "female")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.vitaminAProvided(reportingData);

        verifyBothReportingCalls(VIT_A_2, "2012-01-02");
        verifyBothReportingCalls(VIT_A_2_FOR_FEMALE_CHILD, "2012-01-02");
        verifyBothReportingCalls(VIT_A_FOR_FEMALE, "2012-01-02");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportFifthVitaminDoseDuringImmunizationProvidedForFemaleChild() throws Exception {
        SafeMap reportingData = reportDataForVitaminA("5", "2012-01-02");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "female")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.vitaminAProvided(reportingData);

        verifyBothReportingCalls(VIT_A_FOR_FEMALE, "2012-01-02");
        verifyBothReportingCalls(VIT_A_5_FOR_FEMALE_CHILD, "2012-01-02");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportNinthVitaminDoseDuringImmunizationProvidedForFemaleChild() throws Exception {
        SafeMap reportingData = reportDataForVitaminA("9", "2012-01-02");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "female")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.vitaminAProvided(reportingData);

        verifyBothReportingCalls(VIT_A_FOR_FEMALE, "2012-01-02");
        verifyBothReportingCalls(VIT_A_9_FOR_FEMALE_CHILD, "2012-01-02");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportFirstVitaminDoseDuringImmunizationProvidedForMaleChild() throws Exception {
        SafeMap reportingData = reportDataForVitaminA("1", "2012-01-02");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "male")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.vitaminAProvided(reportingData);

        verifyBothReportingCalls(VIT_A_1, "2012-01-02");
        verifyBothReportingCalls(VIT_A_1_FOR_MALE_CHILD, "2012-01-02");
        verifyBothReportingCalls(VIT_A_FOR_MALE, "2012-01-02");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportSecondVitaminDoseDuringImmunizationProvidedForMaleChild() throws Exception {
        SafeMap reportingData = reportDataForVitaminA("2", "2012-01-02");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "male")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.vitaminAProvided(reportingData);

        verifyBothReportingCalls(VIT_A_2, "2012-01-02");
        verifyBothReportingCalls(VIT_A_2_FOR_MALE_CHILD, "2012-01-02");
        verifyBothReportingCalls(VIT_A_FOR_MALE, "2012-01-02");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportFifthVitaminDoseDuringImmunizationProvidedForMaleChild() throws Exception {
        SafeMap reportingData = reportDataForVitaminA("5", "2012-01-02");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "male")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.vitaminAProvided(reportingData);

        verifyBothReportingCalls(VIT_A_5_FOR_MALE_CHILD, "2012-01-02");
        verifyBothReportingCalls(VIT_A_FOR_MALE, "2012-01-02");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportNinthVitaminDoseDuringImmunizationProvidedForMaleChild() throws Exception {
        SafeMap reportingData = reportDataForVitaminA("9", "2012-01-02");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "male")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.vitaminAProvided(reportingData);

        verifyBothReportingCalls(VIT_A_FOR_MALE, "2012-01-02");
        verifyBothReportingCalls(VIT_A_9_FOR_MALE_CHILD, "2012-01-02");
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldNotReportThirdVitaminDoseDuringImmunizationProvidedForMaleChild() throws Exception {
        SafeMap reportingData = reportDataForVitaminA("3", "2012-01-02");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "male")
                .withAnm("ANM X")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.vitaminAProvided(reportingData);

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
        reportData.put("childId", "CASE X");
        reportData.put("didBreastfeedingStart", "");
        service.registerChild(reportData);

        verifyBothReportingCalls(OPV, "2012-01-01");
    }

    @Test
    public void shouldReportInfantRegistrationAndInfantTotalBalance() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap();
        reportData.put("childId", "CASE X");
        reportData.put("didBreastfeedingStart", "");
        service.registerChild(reportData);

        verifyBothReportingCalls(INFANT_REGISTRATION, "2012-01-01");
        verifyBothReportingCalls(INFANT_BALANCE_TOTAL, "2012-01-01");
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
        reportData.put("childId", "CASE X");
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
        reportData.put("childId", "CASE X");
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
        reportData.put("childId", "CASE X");
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
        reportData.put("childId", "CASE X");
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
        reportData.put("childId", "CASE X");
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
        reportData.put("childId", "CASE X");
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
        reportData.put("childId", "CASE X");
        reportData.put("didBreastfeedingStart", "");
        service.registerChild(reportData);

        verifyNoReportingCalls(LBW, "2012-01-01");
    }

    @Test
    public void shouldReportCloseChildCaseWhenReasonIsDeath() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-03-05"));

        verifyBothReportingCalls(INFANT_MORTALITY, "2012-03-05");
    }

    @Test
    public void shouldReportEarlyNeonatalMortalityDeath() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-07"));

        verifyBothReportingCalls(ENM, "2012-01-07");
    }

    @Test
    public void shouldNotReportEarlyNeonatalMortalityDeathIfDateOfDeathIsAfterOneWeekOfBirth() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-08"));

        verifyNoReportingCalls(ENM, "2012-01-08");
    }

    @Test
    public void shouldReportNeonatalMortalityDeath() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-28"));

        verifyBothReportingCalls(NM, "2012-01-28");
    }

    @Test
    public void shouldNotReportNeonatalMortalityDeathIfDateOfDeathIsAfterOneWeekOfBirth() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-29"));

        verifyNoReportingCalls(NM, "2012-01-29");
    }

    @Test
    public void shouldReportLateMortalityWithin29DaysAnd1YearOfBirth() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-29"));
        service.closeChild(reportDataForCloseChild("death_of_child", "2012-12-31"));

        verifyBothReportingCalls(LNM, "2012-01-29");
        verifyBothReportingCalls(LNM, "2012-12-31");
    }

    @Test
    public void shouldNotReportLateMortalityIfNotWithin29DaysAnd1YearOfBirth() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-28"));
        service.closeChild(reportDataForCloseChild("death_of_child", "2013-01-01"));

        verifyNoReportingCalls(LNM, "2012-01-28");
        verifyNoReportingCalls(LNM, "2013-12-31");
    }

    @Test
    public void shouldReportChildMortalityWithin7DaysAnd1YearOfBirth() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("death_of_child", "2012-01-08"));
        service.closeChild(reportDataForCloseChild("death_of_child", "2012-12-31"));

        verifyNoReportingCalls(INFANT_MORTALITY, "2012-01-07");
        verifyNoReportingCalls(INFANT_MORTALITY, "2013-01-01");
    }

    @Test
    public void shouldReportChildMortalityWithin5YearOfBirth() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("death_of_child", "2016-12-31"));

        verifyBothReportingCalls(CHILD_MORTALITY, "2016-12-31");
    }

    @Test
    public void shouldNotReportChildMortalityAfter5YearOfBirth() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("death_of_child", "2017-01-01"));

        verifyNoReportingCalls(CHILD_MORTALITY, "2017-01-01");
    }

    @Test
    public void shouldNotReportCloseChildCaseWhenReasonIsNotDeath() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("child_over5", "2012-03-05"));

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldReportChildDeathDueToDiarrheaWhenChildIsClosedWithCauseOfDeathAsDiarrhea() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("death_of_child", "2016-12-31", "diarrhea"));

        verifyBothReportingCalls(CHILD_MORTALITY_DUE_TO_DIARRHEA, "2016-12-31");
    }

    @Test
    public void shouldNotReportChildDeathDueToDiarrheaWhenChildIsClosedForOtherReasons() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("other_reason", "2016-12-31", "diarrhea"));

        verifyNoReportingCalls(CHILD_MORTALITY_DUE_TO_DIARRHEA, "2016-12-31");
    }

    @Test
    public void shouldNotReportChildDeathDueToDiarrheaWhenChildIsClosedWithDeathCauseNotAsDiarrhea() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.closeChild(reportDataForCloseChild("other_reason", "2016-12-31", "sepsis"));

        verifyNoReportingCalls(CHILD_MORTALITY_DUE_TO_DIARRHEA, "2016-12-31");
    }

    @Test
    public void shouldReportChildDiarrheaEpisodeWhenPNCVisitHappens() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.pncVisitHappened(new SafeMap(EasyMap.create("pncVisitDate", "2012-01-01")
                .put("id", "MOTHER-CASE-1")
                .put("childId", "CASE X")
                .put("urineStoolProblems", "diarrhea")
                .put("deliveryPlace", "home")
                .put("referenceDate", "2012-01-01")
                .put("pncVisitDate", "2012-01-01")
                .map()));

        verifyBothReportingCalls(CHILD_DIARRHEA, "2012-01-01");
    }

    @Test
    public void shouldNotReportDiarrheaEpisodeWhenPNCVisitHappensAndThereAreNoUrineStoolProblems() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.pncVisitHappened(new SafeMap(EasyMap.create("pncVisitDate", "2012-01-01")
                .put("id", "MOTHER-CASE-1")
                .put("childId", "CASE X")
                .put("urineStoolProblems", null)
                .put("deliveryPlace", "home")
                .put("referenceDate", "2012-01-01")
                .put("pncVisitDate", "2012-01-01")
                .map()));

        verifyNoReportingCalls(CHILD_DIARRHEA, "2012-01-01");
    }

    @Test
    public void shouldNotReportDiarrheaEpisodeWhenPNCVisitHappensAndChildDoesNotHaveDiarrhea() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.pncVisitHappened(new SafeMap(EasyMap.create("pncVisitDate", "2012-01-01")
                .put("id", "MOTHER-CASE-1")
                .put("childId", "CASE X")
                .put("urineStoolProblems", "vomiting")
                .put("deliveryPlace", "home")
                .put("referenceDate", "2012-01-01")
                .put("pncVisitDate", "2012-01-01")
                .map()));

        verifyNoReportingCalls(CHILD_DIARRHEA, "2012-01-01");
    }

    @Test
    public void shouldReportPNCVisitThatHappenOnTheSameDayOfHomeDelivery() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.pncVisitHappened(new SafeMap(EasyMap.create("pncVisitDate", "2012-01-01")
                .put("id", "MOTHER-CASE-1")
                .put("childId", "CASE X")
                .put("urineStoolProblems", "diarrhea")
                .put("deliveryPlace", "home")
                .put("referenceDate", "2012-01-01")
                .put("pncVisitDate", "2012-01-01")
                .map()));

        verifyBothReportingCalls(NRHM_PNC24, "2012-01-01");
    }

    @Test
    public void shouldReportPNCVisitThatHappenNextDayOfHomeDelivery() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.pncVisitHappened(new SafeMap(EasyMap.create("pncVisitDate", "2012-01-01")
                .put("id", "MOTHER-CASE-1")
                .put("childId", "CASE X")
                .put("urineStoolProblems", "diarrhea")
                .put("deliveryPlace", "home")
                .put("referenceDate", "2012-01-01")
                .put("pncVisitDate", "2012-01-02")
                .map()));

        verifyBothReportingCalls(NRHM_PNC24, "2012-01-02");
    }

    @Test
    public void shouldNotReportPNCVisitThatHappenTwoOrMoreDaysOfHomeDelivery() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.pncVisitHappened(new SafeMap(EasyMap.create("pncVisitDate", "2012-01-01")
                .put("id", "MOTHER-CASE-1")
                .put("childId", "CASE X")
                .put("urineStoolProblems", "diarrhea")
                .put("deliveryPlace", "home")
                .put("referenceDate", "2012-01-01")
                .put("pncVisitDate", "2012-01-03")
                .map()));

        verifyNoReportingCalls(NRHM_PNC24, "2012-01-01");
    }

    @Test
    public void shouldNotReportPNCVisitForNonHomeDelivery() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.pncVisitHappened(new SafeMap(EasyMap.create("pncVisitDate", "2012-01-01")
                .put("id", "MOTHER-CASE-1")
                .put("childId", "CASE X")
                .put("urineStoolProblems", "diarrhea")
                .put("deliveryPlace", "phc")
                .put("referenceDate", "2012-01-01")
                .put("pncVisitDate", "2012-01-03")
                .map()));

        verifyNoReportingCalls(NRHM_PNC24, "2012-01-01");
    }

    @Test
    public void shouldReportChildDiarrheaEpisodeWhenSickVisitHappens() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.sickVisitHappened(new SafeMap(EasyMap.create("sickVisitDate", "2012-01-01")
                .put("id", "CASE X")
                .put("childSigns", "diarrhea")
                .put("submissionDate", "2012-01-01")
                .map()));

        verifyBothReportingCalls(CHILD_DIARRHEA, "2012-01-01");
    }

    @Test
    public void shouldNotReportDiarrheaEpisodeWhenSickVisitHappensAndChildDoesNotHaveDiarrhea() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.sickVisitHappened(new SafeMap(EasyMap.create("sickVisitDate", "2012-01-01")
                .put("id", "CASE X")
                .put("childSigns", null)
                .put("reportChildDisease", null)
                .put("submissionDate", "2012-01-01")
                .map()));

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldReportChildDiarrheaEpisodeWhenSickVisitHappensAndChildHasDiarrhea() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01")
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.sickVisitHappened(new SafeMap(EasyMap.create("reportChildDiseaseDate", "2012-01-01")
                .put("id", "CASE X")
                .put("childSigns", null)
                .put("reportChildDisease", "diarrhea_dehydration")
                .put("submissionDate", "2012-01-01")
                .map()));

        verifyBothReportingCalls(CHILD_DIARRHEA, "2012-01-01");
    }

    @Test
    public void shouldNotReportChildDiarrheaEpisodeWhenSickVisitHappensAndChildAgeIsGreaterThanFive() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "MOTHER-CASE-1", "opv_0", "5", "female")
                .withAnm("ANM X")
                .withDateOfBirth(parse("2012-01-01").minusYears(5).minusDays(1).toString())
                .withThayiCard("TC 1"));
        when(allMothers.findByCaseId("MOTHER-CASE-1")).thenReturn(new Mother("MOTHER-CASE-1", "EC-CASE-1", "TC 1"));
        when(allEligibleCouples.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));

        service.sickVisitHappened(new SafeMap(EasyMap.create("reportChildDiseaseDate", "2012-01-01")
                .put("id", "CASE X")
                .put("childSigns", null)
                .put("reportChildDisease", "diarrhea_dehydration")
                .put("submissionDate", "2012-01-01")
                .map()));

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldReportAllChildrenLessThanOneYearOldAtTheBeginningOfReportMonthAsInfantBalanceOnHandWhenReportingInfantBalanceAndItIsNotReportedBeforeInThatMonth()
            throws Exception {
        DateUtil.fakeIt(parse("2013-02-01"));
        LocalDate previousReportMonthStartDate = LocalDate.parse("2012-12-26");
        InfantBalanceOnHandReportToken infantBalanceOnHandReportToken = new InfantBalanceOnHandReportToken(previousReportMonthStartDate);
        when(allInfantBalanceOnHandTokens.getAll()).thenReturn(asList(infantBalanceOnHandReportToken));
        String currentReportMonthStartDate = "2013-01-26";
        when(allChildren.findAllChildrenLessThanOneYearOldAsOfDate(parse(currentReportMonthStartDate)))
                .thenReturn(asList(
                        new Child("child id 1", "mother id 1", "", "5", "male").withThayiCard("thayi card 1").withAnm("ANM X"),
                        new Child("child id 2", "mother id 2", "", "6", "female").withThayiCard("thayi card 2").withAnm("ANM X")));
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "ec id 1", "thayi card 1"));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));
        when(allMothers.findByCaseId("mother id 2")).thenReturn(new Mother("mother id 2", "ec id 2", "thayi card 2"));
        when(allEligibleCouples.findByCaseId("ec id 2")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.startOfCurrentReportMonth(parse("2013-02-01"))).thenReturn(parse("2013-01-26"));

        service.reportInfantBalanceOnHand();

        verifyBothReportingCalls(Indicator.INFANT_BALANCE_ON_HAND, currentReportMonthStartDate, "child id 1", "thayi card 1");
        verifyBothReportingCalls(Indicator.INFANT_BALANCE_ON_HAND, currentReportMonthStartDate, "child id 2", "thayi card 2");
        verifyBothReportingCalls(Indicator.INFANT_BALANCE_TOTAL, currentReportMonthStartDate, "child id 1", "thayi card 1");
        verifyBothReportingCalls(Indicator.INFANT_BALANCE_TOTAL, currentReportMonthStartDate, "child id 2", "thayi card 2");
        verify(allInfantBalanceOnHandTokens).update(new InfantBalanceOnHandReportToken(parse("2013-02-01")));
    }

    @Test
    public void shouldReportInfantBalanceOnHandWhenNothingIsReportedSoFar()
            throws Exception {
        DateUtil.fakeIt(parse("2013-02-01"));
        when(allInfantBalanceOnHandTokens.getAll()).thenReturn(Collections.<InfantBalanceOnHandReportToken>emptyList());
        String currentReportMonthStartDate = "2013-01-26";
        when(allChildren.findAllChildrenLessThanOneYearOldAsOfDate(parse(currentReportMonthStartDate)))
                .thenReturn(asList(
                        new Child("child id 1", "mother id 1", "", "5", "male").withThayiCard("thayi card 1").withAnm("ANM X"),
                        new Child("child id 2", "mother id 2", "", "6", "female").withThayiCard("thayi card 2").withAnm("ANM X")));
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "ec id 1", "thayi card 1"));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));
        when(allMothers.findByCaseId("mother id 2")).thenReturn(new Mother("mother id 2", "ec id 2", "thayi card 2"));
        when(allEligibleCouples.findByCaseId("ec id 2")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.startOfCurrentReportMonth(parse("2013-02-01"))).thenReturn(parse("2013-01-26"));

        service.reportInfantBalanceOnHand();

        verifyBothReportingCalls(Indicator.INFANT_BALANCE_ON_HAND, currentReportMonthStartDate, "child id 1", "thayi card 1");
        verifyBothReportingCalls(Indicator.INFANT_BALANCE_ON_HAND, currentReportMonthStartDate, "child id 2", "thayi card 2");
        verifyBothReportingCalls(Indicator.INFANT_BALANCE_TOTAL, currentReportMonthStartDate, "child id 1", "thayi card 1");
        verifyBothReportingCalls(Indicator.INFANT_BALANCE_TOTAL, currentReportMonthStartDate, "child id 2", "thayi card 2");
        verify(allInfantBalanceOnHandTokens).update(new InfantBalanceOnHandReportToken(parse("2013-02-01")));
    }

    @Test
    public void shouldNotReportInfantBalanceOnHandWhenItHasBeenReportedAlreadyForCurrentReportMonth()
            throws Exception {
        DateUtil.fakeIt(parse("2013-02-01"));
        String currentReportMonthStartDate = "2013-01-26";
        when(allInfantBalanceOnHandTokens.getAll()).thenReturn(asList(new InfantBalanceOnHandReportToken(parse(currentReportMonthStartDate))));
        when(allChildren.findAllChildrenLessThanOneYearOldAsOfDate(parse(currentReportMonthStartDate)))
                .thenReturn(asList(
                        new Child("child id 1", "mother id 1", "", "5", "male").withThayiCard("thayi card 1").withAnm("ANM X"),
                        new Child("child id 2", "mother id 2", "", "6", "female").withThayiCard("thayi card 2").withAnm("ANM X")));
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "ec id 1", "thayi card 1"));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));
        when(allMothers.findByCaseId("mother id 2")).thenReturn(new Mother("mother id 2", "ec id 2", "thayi card 2"));
        when(allEligibleCouples.findByCaseId("ec id 2")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.startOfCurrentReportMonth(parse("2013-02-01"))).thenReturn(parse("2013-01-26"));
        when(reportMonth.isDateWithinCurrentReportMonth(parse(currentReportMonthStartDate))).thenReturn(true);

        service.reportInfantBalanceOnHand();

        verifyNoReportingCalls(Indicator.INFANT_BALANCE_ON_HAND, currentReportMonthStartDate, "child id 1", "thayi card 1");
        verifyNoReportingCalls(Indicator.INFANT_BALANCE_ON_HAND, currentReportMonthStartDate, "child id 2", "thayi card 2");
        verifyNoReportingCalls(Indicator.INFANT_BALANCE_TOTAL, currentReportMonthStartDate, "child id 1", "thayi card 1");
        verifyNoReportingCalls(Indicator.INFANT_BALANCE_TOTAL, currentReportMonthStartDate, "child id 2", "thayi card 2");
    }

    @Test
    public void shouldReportTotalNumberOfOAChildrenWhenInfantBalanceIsReported() throws Exception {
        DateUtil.fakeIt(parse("2013-02-01"));
        Mother mother = new Mother("mother id 1", "ec id 1", "thayi card 1");
        Child child = new Child("child id 1", "mother id 1", "bcg", "3", "female").withThayiCard("thayi card 1").withAnm("ANM X");
        EligibleCouple ec = new EligibleCouple("ec id 1", "123").withLocation("bherya", "Sub Center", "PHC X").asOutOfArea();
        when(allEligibleCouples.findAllOutOfAreaCouples()).thenReturn(asList(ec));
        when(allMothers.findAllOpenMothersByECCaseId(asList("ec id 1"))).thenReturn(asList(mother));
        when(allMothers.findAllOpenMothersByECCaseId(asList("ec id 1"))).thenReturn(asList(mother));
        when(allChildren.findAllOpenChildrenByMotherId(asList("mother id 1"))).thenReturn(asList(child));
        when(reportMonth.startOfCurrentReportMonth(parse("2013-02-01"))).thenReturn(parse("2013-01-26"));
        when(reportMonth.endOfCurrentReportMonth(parse("2013-02-01"))).thenReturn(parse("2013-02-25"));

        service.reportInfantBalanceTotalNumberOfOAChildren();

        verifyBothUpdateReportCalls(Indicator.INFANT_BALANCE_OA_CHILDREN, "2013-02-01", "child id 1", "thayi card 1",
                "2013-01-26", "2013-02-25");
    }

    @Test
    public void shouldReportTotalNumberOfChildrenLessThanOneYearOldWhenInfantBalanceIsReported()
            throws Exception {
        LocalDate today = parse("2013-02-01");
        DateUtil.fakeIt(today);
        when(allChildren.findAllChildrenLessThanOneYearOldAsOfDate(today))
                .thenReturn(asList(
                        new Child("child id 1", "mother id 1", "", "5", "male").withThayiCard("thayi card 1").withAnm("ANM X")));
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "ec id 1", "thayi card 1"));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.startOfCurrentReportMonth(today)).thenReturn(parse("2013-01-26"));
        when(reportMonth.startOfCurrentReportMonth(parse("2013-01-26"))).thenReturn(parse("2013-01-26"));
        when(reportMonth.endOfCurrentReportMonth(parse("2013-01-26"))).thenReturn(parse("2013-02-25"));

        service.reportInfantBalanceLessThanOneYearOld();

        verifyBothUpdateReportCalls(Indicator.INFANT_BALANCE_LESS_THAN_ONE_YEAR, "2013-01-26", "child id 1", "thayi card 1", "2013-01-26", "2013-02-25");
        verifyBothUpdateReportCalls(Indicator.INFANT_BALANCE_BALANCE, "2013-01-26", "child id 1", "thayi card 1", "2013-01-26", "2013-02-25");
    }

    @Test
    public void shouldReportTotalNumberOfChildrenLessThanFiveYearOldWhenInfantBalanceIsReported()
            throws Exception {
        LocalDate today = parse("2013-02-01");
        DateUtil.fakeIt(today);
        when(allChildren.findAllChildrenLessThanFiveYearOldAsOfDate(today))
                .thenReturn(asList(
                        new Child("child id 1", "mother id 1", "", "5", "male").withThayiCard("thayi card 1").withAnm("ANM X")));
        when(allMothers.findByCaseId("mother id 1")).thenReturn(new Mother("mother id 1", "ec id 1", "thayi card 1"));
        when(allEligibleCouples.findByCaseId("ec id 1")).thenReturn(new EligibleCouple().withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.startOfCurrentReportMonth(today)).thenReturn(parse("2013-01-26"));
        when(reportMonth.startOfCurrentReportMonth(parse("2013-01-26"))).thenReturn(parse("2013-01-26"));
        when(reportMonth.endOfCurrentReportMonth(parse("2013-01-26"))).thenReturn(parse("2013-02-25"));

        service.reportInfantBalanceLessThanFiveYearOld();

        verifyBothUpdateReportCalls(Indicator.INFANT_BALANCE_LESS_THAN_FIVE_YEAR, "2013-01-26", "child id 1", "thayi card 1", "2013-01-26", "2013-02-25");
    }

    private void assertIndicatorBasedOnImmunization(String immunizationProvided, Indicator... expectedIndicators) {
        ReportingService fakeReportingService = mock(ReportingService.class);
        ChildReportingService childReportingService = new ChildReportingService(fakeReportingService, allChildren, allMothers, allEligibleCouples, allInfantBalanceOnHandTokens, reportMonth);
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

    private SafeMap reportDataForVitaminA(String vitaminADose, String vitaminADate) {
        SafeMap reportingData = new SafeMap();
        reportingData.put("id", "CASE X");
        reportingData.put("vitaminADose", vitaminADose);
        reportingData.put("vitaminADate", vitaminADate);
        return reportingData;
    }

    private SafeMap reportDataForCloseChild(String closeReason, String closeDate) {
        return reportDataForCloseChild(closeReason, closeDate, null);
    }

    private SafeMap reportDataForCloseChild(String closeReason, String closeDate, String deathCause) {
        SafeMap reportingData = new SafeMap();
        reportingData.put("id", "CASE X");
        reportingData.put("closeReason", closeReason);
        reportingData.put("deathDate", closeDate);
        reportingData.put("deathCause", deathCause);
        return reportingData;
    }

    private void verifyBothReportingCalls(Indicator indicator, String date) {
        verifyBothReportingCalls(indicator, date, "CASE X", "TC 1");
    }

    private void verifyBothReportingCalls(Indicator indicator, String date, String externalIdForANMReport, String externalIdForServiceProvidedReport) {
        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X",
                externalIdForServiceProvidedReport, indicator, date, new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", externalIdForANMReport, indicator, date);
        verify(reportingService).sendReportData(serviceProvidedData);
        verify(reportingService).sendReportData(anmReportData);
    }

    private void verifyBothUpdateReportCalls(Indicator indicator, String date, String externalIdForANMReport,
                                             String externalIdForServiceProvidedReport, String startDate, String endDate) {
        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X",
                externalIdForServiceProvidedReport, indicator, date, new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", externalIdForANMReport, indicator, date);
        ReportDataUpdateRequest serviceProvidedUpdateRequest = new ReportDataUpdateRequest()
                .withType(SERVICE_PROVIDER_TYPE)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withReportingData(asList(serviceProvidedData))
                .withIndicator(indicator.value());
        ReportDataUpdateRequest anmReportUpdateRequest = new ReportDataUpdateRequest()
                .withType(ANM_REPORT_DATA_TYPE)
                .withStartDate(startDate)
                .withEndDate(endDate)
                .withReportingData(asList(anmReportData))
                .withIndicator(indicator.value());

        verify(reportingService).updateReportData(serviceProvidedUpdateRequest);
        verify(reportingService).updateReportData(anmReportUpdateRequest);
    }

    private void verifyNoReportingCalls(Indicator indicator, String date) {
        verifyNoReportingCalls(indicator, date, "TC 1", "CASE X");
    }

    private void verifyNoReportingCalls(Indicator indicator, String date, String externalIdForANMReport, String externalIdForServiceProvidedReport) {
        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", externalIdForServiceProvidedReport, indicator, date, new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", externalIdForANMReport, indicator, date);
        verify(reportingService, times(0)).sendReportData(serviceProvidedData);
        verify(reportingService, times(0)).sendReportData(anmReportData);
    }
}
