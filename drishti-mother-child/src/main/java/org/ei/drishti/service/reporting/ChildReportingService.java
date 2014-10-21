package org.ei.drishti.service.reporting;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportMonth;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.domain.*;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllInfantBalanceOnHandReportTokens;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

import static ch.lambdaj.Lambda.*;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.drishti.common.AllConstants.ANCFormFields.REGISTRATION_DATE;
import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.*;
import static org.ei.drishti.common.AllConstants.ChildIllnessFields.*;
import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.*;
import static org.ei.drishti.common.AllConstants.ChildRegistrationFormFields.BF_POSTBIRTH;
import static org.ei.drishti.common.AllConstants.CommonFormFields.*;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.DELIVERY_PLACE;
import static org.ei.drishti.common.AllConstants.EntityCloseFormFields.WRONG_ENTRY_VALUE;
import static org.ei.drishti.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.URINE_STOOL_PROBLEMS;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.VISIT_DATE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.Report.*;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.ANM_REPORT_DATA_TYPE;
import static org.ei.drishti.common.AllConstants.ReportDataParameters.SERVICE_PROVIDED_DATA_TYPE;
import static org.ei.drishti.common.AllConstants.VitaminAFields.*;
import static org.ei.drishti.common.domain.Indicator.*;
import static org.ei.drishti.common.domain.ReportDataDeleteRequest.anmReportDataDeleteRequest;
import static org.ei.drishti.common.domain.ReportDataDeleteRequest.serviceProvidedDataDeleteRequest;
import static org.ei.drishti.common.domain.ReportDataUpdateRequest.buildReportDataRequest;
import static org.ei.drishti.common.domain.ReportingData.anmReportData;
import static org.ei.drishti.common.domain.ReportingData.serviceProvidedData;
import static org.hamcrest.Matchers.equalTo;
import static org.joda.time.LocalDate.parse;

@Service
public class ChildReportingService {
    public static final String CHILD_ID_FIELD = "childId";
    private static Logger logger = LoggerFactory.getLogger(ChildReportingService.class.toString());
    private final ReportingService reportingService;
    private final AllChildren allChildren;
    private final AllMothers allMothers;
    private final AllEligibleCouples allEligibleCouples;
    private final AllInfantBalanceOnHandReportTokens allInfantBalanceOnHandReportTokens;
    private final ReportMonth reportMonth;
    private MCTSReporter mctsReporter;
    private Map<String, List<Indicator>> immunizationToIndicator;

    @Autowired
    public ChildReportingService(ReportingService reportingService, AllChildren allChildren, AllMothers allMothers,
                                 AllEligibleCouples allEligibleCouples,
                                 AllInfantBalanceOnHandReportTokens allInfantBalanceOnHandReportTokens, ReportMonth reportMonth, MCTSReporter mctsReporter) {
        this.reportingService = reportingService;
        this.allChildren = allChildren;
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
        this.allInfantBalanceOnHandReportTokens = allInfantBalanceOnHandReportTokens;
        this.reportMonth = reportMonth;
        this.mctsReporter = mctsReporter;

        immunizationToIndicator = new HashMap<>();

        immunizationToIndicator.put(BCG_VALUE, asList(BCG));

        immunizationToIndicator.put(DPT_BOOSTER_1_VALUE, asList(DPT, DPT_BOOSTER_OR_OPV_BOOSTER, DPT_BOOSTER1));
        immunizationToIndicator.put(DPT_BOOSTER_2_VALUE, asList(DPT_BOOSTER2));

        immunizationToIndicator.put(HEPATITIS_0_VALUE, asList(HEP));

        immunizationToIndicator.put(OPV_0_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_1_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_2_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_3_VALUE, asList(OPV, PENTAVALENT3_OR_OPV3, OPV3));
        immunizationToIndicator.put(OPV_BOOSTER_VALUE, asList(OPV, DPT_BOOSTER_OR_OPV_BOOSTER, OPV_BOOSTER));

        immunizationToIndicator.put(MEASLES_VALUE, asList(MEASLES));

        immunizationToIndicator.put(PENTAVALENT_1_VALUE, asList(PENT1));
        immunizationToIndicator.put(PENTAVALENT_2_VALUE, asList(PENT2));
        immunizationToIndicator.put(PENTAVALENT_3_VALUE, asList(PENT3, PENTAVALENT3_OR_OPV3));

        immunizationToIndicator.put(MMR_VALUE, asList(MMR));
        immunizationToIndicator.put(JE_VALUE, asList(JE));
    }

    public void registerChild(SafeMap reportData) {
        String id = reportData.get(ChildReportingService.CHILD_ID_FIELD);
        Child child = allChildren.findByCaseId(id);

        List<String> immunizations = child.immunizationsGiven();

        Location location = loadLocationOfChild(child);
        String submissionDate = reportData.get(SUBMISSION_DATE_FIELD_NAME);
        reportImmunizations(child, immunizations, location, child.dateOfBirth(), submissionDate);
        reportNRHMImmunizations(reportData, child, immunizations, location);
        reportBirthWeight(child, submissionDate, location);
        reportBFPostBirth(reportData.get(BF_POSTBIRTH), child, location, reportData.get(DELIVERY_PLACE), submissionDate);
        reportToBoth(child, INFANT_REGISTRATION, child.dateOfBirth(), submissionDate, location);
        reportLiveBirthByGender(reportData, child, location);
        reportToBoth(child, INFANT_BALANCE_TOTAL, child.dateOfBirth(), submissionDate, location);
        reportMCTSIndicators(reportData, child, immunizations);
    }

    private void reportMCTSIndicators(SafeMap reportData, Child child, List<String> immunizations) {
        if (immunizations.contains(BCG_VALUE)) {
            mctsReporter.report(child.caseId(), child.thayiCardNumber(), MCTSServiceCode.BCG.toString(), reportData.get(REGISTRATION_DATE), child.dateOfBirth());
        }
        if (immunizations.contains(OPV_0_VALUE)) {
            mctsReporter.report(child.caseId(), child.thayiCardNumber(), MCTSServiceCode.OPV0.toString(), reportData.get(REGISTRATION_DATE), child.dateOfBirth());
        }
        if (immunizations.contains(HEPATITIS_0_VALUE)) {
            mctsReporter.report(child.caseId(), child.thayiCardNumber(), MCTSServiceCode.HEPB0.toString(), reportData.get(REGISTRATION_DATE), child.dateOfBirth());
        }
    }

    private void reportNRHMImmunizations(SafeMap reportData, Child child, List<String> immunizations, Location location) {
        if (reportData.get(DELIVERY_PLACE).equalsIgnoreCase(SUBCENTER_SERVICE_PROVIDED_PLACE_VALUE)) {
            String submissionDate = reportData.get(SUBMISSION_DATE_FIELD_NAME);
            if (immunizations.contains(OPV_0_VALUE)) {
                reportToBoth(child, NRHM_OPV_0_1YR, child.dateOfBirth(), submissionDate, location);
            }
            if (immunizations.contains(BCG_VALUE)) {
                reportToBoth(child, NRHM_BCG_1YR, child.dateOfBirth(), submissionDate, location);
            }
            if (immunizations.contains(HEPATITIS_0_VALUE)) {
                reportToBoth(child, NRHM_HEPB_0_1YR, child.dateOfBirth(), submissionDate, location);
            }

        }
    }

    public void immunizationProvided(SafeMap reportData, List<String> previousImmunizations) {
        Child child = allChildren.findByCaseId(reportData.get(ID));

        List<String> immunizations = new ArrayList<>(asList(reportData.get(IMMUNIZATIONS_GIVEN_FIELD_NAME).split(" ")));
        immunizations.removeAll(previousImmunizations);

        Location location = loadLocationOfChild(child);
        reportImmunizations(child, immunizations, location, reportData.get(IMMUNIZATION_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME));
    }

    public void vitaminAProvided(SafeMap reportData) {
        Child child = allChildren.findByCaseId(reportData.get(ID));
        Location location = loadLocationOfChild(child);
        if (child.isFemale()) {
            reportVitaminADose1ForFemaleChild(reportData, child, location);
            reportVitaminADose2ForFemaleChild(reportData, child, location);
            reportVitaminADose5ForFemaleChild(reportData, child, location);
            reportVitaminADose9ForFemaleChild(reportData, child, location);
            reportForVitaminADose_1_2_5_9(reportData, child, location, VIT_A_FOR_FEMALE);
        } else if (child.isMale()) {
            reportVitaminADose1ForMaleChild(reportData, child, location);
            reportVitaminADose2ForMaleChild(reportData, child, location);
            reportVitaminADose5ForMaleChild(reportData, child, location);
            reportVitaminADose9ForMaleChild(reportData, child, location);
            reportForVitaminADose_1_2_5_9(reportData, child, location, VIT_A_FOR_MALE);
        }
    }

    private void reportForVitaminADose_1_2_5_9(SafeMap reportData, Child child, Location location, Indicator indicator) {
        String vitaminDose = reportData.get(VITAMIN_A_DOSE);
        if (VITAMIN_A_DOSES_1_2_5_9.contains(vitaminDose)) {
            reportToBoth(child, indicator, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    public void pncVisitHappened(SafeMap reportData) {
        String id = reportData.get(CHILD_ID_FIELD);
        Child child = allChildren.findByCaseId(id);
        Location location = loadLocationOfChild(child);

        reportDIarrhea(reportData, child, location);
        reportVisitsThatHappenWithin24HoursOfHomeDelivery(reportData, child, location);
    }

    private void reportVisitsThatHappenWithin24HoursOfHomeDelivery(SafeMap reportData, Child child, Location location) {
        LocalDate deliveryDate = LocalDate.parse(reportData.get(REFERENCE_DATE));
        LocalDate pncVisitDate = LocalDate.parse(reportData.get(AllConstants.PNCVisitFormFields.VISIT_DATE_FIELD_NAME));

        if (HOME_FIELD_VALUE.equalsIgnoreCase(reportData.get(DELIVERY_PLACE))
                && (pncVisitDate.equals(deliveryDate) || pncVisitDate.equals(deliveryDate.plusDays(1)))) {
            reportToBoth(child, NRHM_PNC24, reportData.get(VISIT_DATE_FIELD_NAME), location);
        }
    }

    private void reportDIarrhea(SafeMap reportData, Child child, Location location) {
        String problems = reportData.get(URINE_STOOL_PROBLEMS);
        if (!isBlank(problems) && problems.contains(AllConstants.CommonChildFormFields.DIARRHEA_VALUE)) {
            reportToBoth(child, CHILD_DIARRHEA, reportData.get(VISIT_DATE_FIELD_NAME), location);
        }
    }

    public void sickVisitHappened(SafeMap reportData) {
        String id = reportData.get(ID);
        Child child = allChildren.findByCaseId(id);

        Location location = loadLocationOfChild(child);
        LocalDate childDateOfBirth = parse(child.dateOfBirth());
        String submissionDate = reportData.get(SUBMISSION_DATE_FIELD_NAME);
        if (childDateOfBirth.plusYears(CHILD_DIARRHEA_THRESHOLD_IN_YEARS).isAfter(LocalDate.parse(submissionDate))) {
            if (!isBlank(reportData.get(CHILD_SIGNS)) && reportData.get(CHILD_SIGNS).contains(AllConstants.CommonChildFormFields.DIARRHEA_VALUE)) {
                reportToBoth(child, CHILD_DIARRHEA, reportData.get(SICK_VISIT_DATE), submissionDate, location);
            } else if (!isBlank(reportData.get(REPORT_CHILD_DISEASE)) && reportData.get(REPORT_CHILD_DISEASE).contains(AllConstants.ChildIllnessFields.DIARRHEA_DEHYDRATION_VALUE)) {
                reportToBoth(child, CHILD_DIARRHEA, reportData.get(REPORT_CHILD_DISEASE_DATE), submissionDate, location);
            }
        }
    }

    public void closeChild(SafeMap reportData) {
        String closeReason = reportData.get(CLOSE_REASON_FIELD_NAME);
        if (WRONG_ENTRY_VALUE.equalsIgnoreCase(closeReason)) {
            deleteReports(reportData.get(ID));
            return;
        }

        if (!DEATH_OF_CHILD_VALUE.equals(closeReason)) {
            return;
        }

        Child child = allChildren.findByCaseId(reportData.get(ID));
        Location location = loadLocationOfChild(child);
        LocalDate childDateOfBirth = parse(child.dateOfBirth());
        String diedOn = reportData.get(DATE_OF_DEATH_FIELD_NAME);
        LocalDate diedOnDate = parse(diedOn);

        if (childDateOfBirth.plusDays(CHILD_EARLY_NEONATAL_MORTALITY_THRESHOLD_IN_DAYS).isAfter(diedOnDate)) {
            reportToBoth(child, ENM, diedOn, reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
        if (childDateOfBirth.plusDays(CHILD_NEONATAL_MORTALITY_THRESHOLD_IN_DAYS).isAfter(diedOnDate)) {
            reportToBoth(child, NM, diedOn, reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
            reportToBoth(child, INFANT_MORTALITY, diedOn, reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        } else if (childDateOfBirth.plusYears(INFANT_MORTALITY_THRESHOLD_IN_YEARS).isAfter(diedOnDate)) {
            reportToBoth(child, LNM, diedOn, reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
            reportToBoth(child, INFANT_MORTALITY, diedOn, reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
        if (childDateOfBirth.plusYears(CHILD_MORTALITY_THRESHOLD_IN_YEARS).isAfter(diedOnDate)) {
            reportToBoth(child, CHILD_MORTALITY, diedOn, reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        } else {
            logger.warn("Not reporting for child with CaseID" + child.caseId() + "because child's age is more than " + CHILD_MORTALITY_THRESHOLD_IN_YEARS + " years.");
        }
        if (AllConstants.CommonChildFormFields.DIARRHEA_VALUE.equalsIgnoreCase(reportData.get(DEATH_CAUSE))) {
            reportToBoth(child, CHILD_MORTALITY_DUE_TO_DIARRHEA, diedOn, reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private void reportVitaminADose2ForMaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_2_VALUE.equals(reportData.get(VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_2, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
            reportToBoth(child, VIT_A_2_FOR_MALE_CHILD, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private void reportVitaminADose5ForMaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_5_VALUE.equals(reportData.get(VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_5_FOR_MALE_CHILD, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private void reportVitaminADose9ForMaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_9_VALUE.equals(reportData.get(VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_9_FOR_MALE_CHILD, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private void reportVitaminADose1ForMaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_1_VALUE.equals(reportData.get(VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_1, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
            reportToBoth(child, VIT_A_1_FOR_MALE_CHILD, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private void reportVitaminADose2ForFemaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_2_VALUE.equals(reportData.get(VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_2, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
            reportToBoth(child, VIT_A_2_FOR_FEMALE_CHILD, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private void reportVitaminADose5ForFemaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_5_VALUE.equals(reportData.get(VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_5_FOR_FEMALE_CHILD, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private void reportVitaminADose9ForFemaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_9_VALUE.equals(reportData.get(VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_9_FOR_FEMALE_CHILD, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private void reportVitaminADose1ForFemaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_1_VALUE.equals(reportData.get(VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_1, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
            reportToBoth(child, VIT_A_1_FOR_FEMALE_CHILD, reportData.get(VITAMIN_A_DATE), reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
        }
    }

    private Location loadLocationOfChild(Child child) {
        EligibleCouple couple = getEligibleCouple(child);
        return new Location(couple.village(), couple.subCenter(), couple.phc());
    }

    private void reportBirthWeight(Child child, String submissionDate, Location location) {
        try {
            double birthWeight = Double.parseDouble(child.weight());
            if (birthWeight < LOW_BIRTH_WEIGHT_THRESHOLD) {
                reportToBoth(child, LBW, child.dateOfBirth(), submissionDate, location);
            }
            reportToBoth(child, WEIGHED_AT_BIRTH, child.dateOfBirth(), submissionDate, location);
        } catch (NumberFormatException e) {
            logger.warn("Not reporting: Invalid value received for childWeight : " + child.weight() + " for childId : " + child.caseId());
        }
    }

    private void reportBFPostBirth(String bfPostBirth, Child child, Location location, String deliveryPlace, String submissionDate) {
        if (BOOLEAN_TRUE_VALUE.equalsIgnoreCase(bfPostBirth)) {
            reportToBoth(child, BF_POST_BIRTH, child.dateOfBirth(), submissionDate, location);
            if (SUBCENTER_SERVICE_PROVIDED_PLACE_VALUE.equalsIgnoreCase(deliveryPlace)
                    || HOME_FIELD_VALUE.equalsIgnoreCase(deliveryPlace)) {
                reportToBoth(child, NRHM_BF_POST_BIRTH, child.dateOfBirth(), submissionDate, location);
            }
        }
    }

    private void reportLiveBirthByGender(SafeMap reportData, Child child, Location location) {
        String deliveryPlace = reportData.get(DELIVERY_PLACE);
        String submissionDate = reportData.get(SUBMISSION_DATE_FIELD_NAME);
        if (HOME_FIELD_VALUE.equalsIgnoreCase(deliveryPlace)
                || SUBCENTER_SERVICE_PROVIDED_PLACE_VALUE.equalsIgnoreCase(deliveryPlace)) {
            if (child.isMale()) {
                reportToBoth(child, MALE_LIVE_BIRTH, child.dateOfBirth(), submissionDate, location);
            } else {
                reportToBoth(child, FEMALE_LIVE_BIRTH, child.dateOfBirth(), submissionDate, location);
            }
        }
    }

    private void reportImmunizations(Child child, List<String> immunizations, Location location, String immunizationDate, String submissionDate) {
        for (String immunizationProvidedThisTime : immunizations) {
            List<Indicator> indicators = immunizationToIndicator.get(immunizationProvidedThisTime);
            if (indicators == null) {
                logger.warn("Not reporting: Invalid immunization: " + immunizationProvidedThisTime + " for childCaseId: " +
                        child.caseId() + " with immunizations provided: " + immunizations);
                continue;
            }

            for (Indicator indicator : indicators) {
                reportToBoth(child, indicator, immunizationDate, submissionDate, location);
            }
        }
    }

    public void reportToBoth(Child child, Indicator indicator, String serviceProvidedDate, String submissionDate, Location location) {
        if (!reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse(serviceProvidedDate), LocalDate.parse(submissionDate)))
            return;
        report(child, indicator, serviceProvidedDate, location);
    }

    public void reportToBoth(Child child, Indicator indicator, String serviceProvidedDate, Location location) {
        if (!reportMonth.isDateWithinCurrentReportMonth(LocalDate.parse(serviceProvidedDate)))
            return;
        report(child, indicator, serviceProvidedDate, location);
    }

    private void report(Child child, Indicator indicator, String serviceProvidedDate, Location location) {
        String externalId = child.thayiCardNumber();

        //#TODO: Refactor to avoid DB calls
        if (isBlank(externalId)) {
            EligibleCouple ec = getEligibleCouple(child);
            externalId = ec.ecNumber();
        }
        ReportingData serviceProvidedData = serviceProvidedData(child.anmIdentifier(), externalId, indicator, serviceProvidedDate, location, child.caseId());
        reportingService.sendReportData(serviceProvidedData);
        ReportingData anmReportData = anmReportData(child.anmIdentifier(), child.caseId(), indicator, serviceProvidedDate);
        reportingService.sendReportData(anmReportData);
    }

    public void reportInfantBalance() {
        reportInfantBalanceOnHand();
        reportInfantBalanceTurningOneYearOld();
    }

    public void reportInfantBalanceOnHand() {
        LocalDate today = DateUtil.today();
        InfantBalanceOnHandReportToken infantBalanceOnHandReportToken = getInfantBalanceOnHandReportToken(today);
        if (reportMonth.isDateWithinCurrentReportMonth(infantBalanceOnHandReportToken.getLastReportedDate())) {
            logger.info(MessageFormat.format("Infant Balance (On Hand) was last reported on: {0}, so not reporting now: {1}.",
                    infantBalanceOnHandReportToken.getLastReportedDate(), today));
            return;
        }

        logger.info(MessageFormat.format("Infant Balance (On Hand) was last reported on: {0}, so reporting now: {1}.",
                infantBalanceOnHandReportToken.getLastReportedDate(), today));
        LocalDate startOfCurrentReportMonth = this.reportMonth.startOfCurrentReportMonth(today);
        List<Child> childrenLessThanOneYearOld = allChildren.findAllChildrenLessThanOneYearOldAsOfDate(
                startOfCurrentReportMonth);
        logger.info(MessageFormat.format("Found {0} children for reporting Infant Balance (On Hand) and Infant Balance (Total) ",
                childrenLessThanOneYearOld.size()));
        for (Child child : childrenLessThanOneYearOld) {
            logger.info(MessageFormat.format("Reporting Infant Balance (On Hand) and Infant Balance (Total) on date: {0} for child: {1}.",
                    startOfCurrentReportMonth.toString(), child));
            Location location = loadLocationOfChild(child);
            reportToBoth(child, Indicator.INFANT_BALANCE_ON_HAND,
                    startOfCurrentReportMonth.toString(), location);
            reportToBoth(child, Indicator.INFANT_BALANCE_TOTAL,
                    startOfCurrentReportMonth.toString(), location);
        }
        logger.info(MessageFormat.format("Updating Infant Balance (On Hand) last reported date to {0}", today));
        allInfantBalanceOnHandReportTokens.update(infantBalanceOnHandReportToken.withLastReportedDate(today));
    }

    public void reportInfantBalanceTurningOneYearOld() {
        LocalDate today = DateUtil.today();

        LocalDate startOfCurrentReportMonth = this.reportMonth.startOfCurrentReportMonth(today);
        List<Child> childrenTurnedOneYearOld = allChildren.findAllChildrenWhoTurnedOneYearOld(
                startOfCurrentReportMonth);
        logger.info(MessageFormat.format("Found {0} children for reporting Infant Balance (Turned One Year old)  ",
                childrenTurnedOneYearOld.size()));

        updateInfantBalanceIndicator(Indicator.INFANT_BALANCE_TURNING_ONE_YEAR, childrenTurnedOneYearOld,
                startOfCurrentReportMonth.toString());
    }

    public void reportInfantBalanceTotalNumberOfOAChildren() {
        List<EligibleCouple> outOfAreaCouples = allEligibleCouples.findAllOutOfAreaCouples();
        List<String> ecIds = extract(outOfAreaCouples, on(EligibleCouple.class).caseId());
        List<Mother> outOfAreaMothers = allMothers.findAllOpenMothersByECCaseId(ecIds);
        List<String> motherIds = extract(outOfAreaMothers, on(Mother.class).caseId());
        List<Child> outOfAreaChildren = allChildren.findAllOpenChildrenByMotherId(motherIds);

        logger.info(MessageFormat.format("Found {0} children for reporting Infant Balance (O/A children)  ",
                outOfAreaChildren.size()));

        updateInfantBalanceOAChildren(outOfAreaChildren, outOfAreaMothers, outOfAreaCouples, Indicator.INFANT_BALANCE_OA_CHILDREN, DateUtil.today().toString());
    }

    public void reportInfantBalanceLessThanOneYearOld() {
        LocalDate today = DateUtil.today();

        List<Child> childrenLessThanOneYearOld = allChildren.findAllChildrenLessThanOneYearOldAsOfDate(today);
        logger.info(MessageFormat.format("Found {0} children for reporting Infant Balance (less than one year old)  ",
                childrenLessThanOneYearOld.size()));

        LocalDate startOfCurrentReportMonth = reportMonth.startOfCurrentReportMonth(today);
        updateInfantBalanceIndicator(Indicator.INFANT_BALANCE_LESS_THAN_ONE_YEAR, childrenLessThanOneYearOld,
                startOfCurrentReportMonth.toString());
        updateInfantBalanceIndicator(Indicator.INFANT_BALANCE_BALANCE, childrenLessThanOneYearOld,
                startOfCurrentReportMonth.toString());
    }

    public void reportInfantBalanceLessThanFiveYearOld() {
        LocalDate today = DateUtil.today();

        List<Child> childrenLessThanFiveYearOld = allChildren.findAllChildrenLessThanFiveYearOldAsOfDate(today);
        logger.info(MessageFormat.format("Found {0} children for reporting Infant Balance (less than five year old)  ",
                childrenLessThanFiveYearOld.size()));

        LocalDate startOfCurrentReportMonth = reportMonth.startOfCurrentReportMonth(today);
        updateInfantBalanceIndicator(Indicator.INFANT_BALANCE_LESS_THAN_FIVE_YEAR, childrenLessThanFiveYearOld,
                startOfCurrentReportMonth.toString());
    }

    private InfantBalanceOnHandReportToken getInfantBalanceOnHandReportToken(LocalDate today) {
        InfantBalanceOnHandReportToken infantBalanceOnHandReportToken;
        List<InfantBalanceOnHandReportToken> tokens = allInfantBalanceOnHandReportTokens.getAll();
        if (tokens.isEmpty()) {
            infantBalanceOnHandReportToken = new InfantBalanceOnHandReportToken(
                    reportMonth.startOfCurrentReportMonth(today).minusMonths(1))
                    .withId(UUID.randomUUID().toString());
        } else {
            infantBalanceOnHandReportToken = tokens.get(0);
        }
        return infantBalanceOnHandReportToken;
    }

    private void updateInfantBalanceIndicator(Indicator indicator, List<Child> children, String date) {
        List<ReportingData> serviceProvidedData = new ArrayList<>();
        List<ReportingData> anmReportData = new ArrayList<>();
        for (Child child : children) {

            //#TODO: Refactor to remove the DB calls
            EligibleCouple couple = getEligibleCouple(child);
            String externalId = getExternalId(child, couple);
            Location location = new Location(couple.village(), couple.subCenter(), couple.phc());
            ReportingData serviceProvidedDataForChild = serviceProvidedData(child.anmIdentifier(), externalId, indicator, date, location, child.caseId());
            ReportingData anmReportDataForChild = anmReportData(child.anmIdentifier(), child.caseId(), indicator, date);
            serviceProvidedData.add(serviceProvidedDataForChild);
            anmReportData.add(anmReportDataForChild);
            logger.info(MessageFormat.format("Reporting Indicator: {0} on date: {1} for child: {2}.",
                    indicator, date, child));
        }
        updateBothReports(indicator, date, serviceProvidedData, anmReportData);
    }

    private EligibleCouple getEligibleCouple(Child child) {
        Mother mother = allMothers.findByCaseId(child.motherCaseId());
        return allEligibleCouples.findByCaseId(mother.ecCaseId());
    }

    private void updateInfantBalanceOAChildren(List<Child> children, List<Mother> mothers, List<EligibleCouple> ecs, Indicator indicator, String date) {
        List<ReportingData> serviceProvidedData = new ArrayList<>();
        List<ReportingData> anmReportData = new ArrayList<>();
        for (Child child : children) {
            Mother mother = selectFirst(mothers, having(on(Mother.class).caseId(), equalTo(child.motherCaseId())));
            EligibleCouple ec = selectFirst(ecs, having(on(EligibleCouple.class).caseId(), equalTo(mother.ecCaseId())));
            Location location = ec.location();
            String externalId = getExternalId(child, ec);
            ReportingData serviceProvidedDataForChild = serviceProvidedData(child.anmIdentifier(), externalId, indicator, date, location, child.caseId());
            ReportingData anmReportDataForChild = anmReportData(child.anmIdentifier(), child.caseId(), indicator, date);
            serviceProvidedData.add(serviceProvidedDataForChild);
            anmReportData.add(anmReportDataForChild);
            logger.info(MessageFormat.format("Reporting Infant Balance (O/A children) on date: {0} for child: {1}.",
                    date, child));
        }
        updateBothReports(indicator, date, serviceProvidedData, anmReportData);
    }

    private String getExternalId(Child child, EligibleCouple ec) {
        String externalId = child.thayiCardNumber();
        if (isBlank(externalId)) {
            externalId = ec.ecNumber();
        }
        return externalId;
    }

    private void updateBothReports(Indicator indicator, String date, List<ReportingData> serviceProvidedData, List<ReportingData> anmReportData) {
        if (!isBlank(date) && !reportMonth.isDateWithinCurrentReportMonth(LocalDate.parse(date)))
            return;
        LocalDate reportingDate = LocalDate.parse(date);
        String reportingMonthStartDate = reportMonth.startOfCurrentReportMonth(reportingDate).toString();
        String reportingMonthEndDate = reportMonth.endOfCurrentReportMonth(reportingDate).toString();
        reportingService.updateReportData(buildReportDataRequest(SERVICE_PROVIDED_DATA_TYPE, indicator, reportingMonthStartDate, reportingMonthEndDate, serviceProvidedData));
        reportingService.updateReportData(buildReportDataRequest(ANM_REPORT_DATA_TYPE, indicator, reportingMonthStartDate, reportingMonthEndDate, anmReportData));
    }

    private void deleteReports(String childCaseId) {
        reportingService.deleteReportData(serviceProvidedDataDeleteRequest(childCaseId));
        reportingService.deleteReportData(anmReportDataDeleteRequest(childCaseId));
    }
}
