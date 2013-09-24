package org.ei.drishti.service.reporting;

import org.apache.commons.lang3.StringUtils;
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

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.*;
import static org.ei.drishti.common.AllConstants.ChildIllnessFields.*;
import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.*;
import static org.ei.drishti.common.AllConstants.ChildRegistrationFormFields.BF_POSTBIRTH;
import static org.ei.drishti.common.AllConstants.CommonFormFields.ID;
import static org.ei.drishti.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.URINE_STOOL_PROBLEMS;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.VISIT_DATE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.Report.*;
import static org.ei.drishti.common.AllConstants.VitaminAFields.*;
import static org.ei.drishti.common.domain.Indicator.*;
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
    private final ReportMonth reportMonth = new ReportMonth();
    private Map<String, List<Indicator>> immunizationToIndicator;

    @Autowired
    public ChildReportingService(ReportingService reportingService, AllChildren allChildren, AllMothers allMothers,
                                 AllEligibleCouples allEligibleCouples,
                                 AllInfantBalanceOnHandReportTokens allInfantBalanceOnHandReportTokens) {
        this.reportingService = reportingService;
        this.allChildren = allChildren;
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
        this.allInfantBalanceOnHandReportTokens = allInfantBalanceOnHandReportTokens;
        immunizationToIndicator = new HashMap<>();

        immunizationToIndicator.put(BCG_VALUE, asList(BCG));

        immunizationToIndicator.put(DPT_BOOSTER_1_VALUE, asList(DPT, DPT_BOOSTER_OR_OPV_BOOSTER));
        immunizationToIndicator.put(DPT_BOOSTER_2_VALUE, asList(DPT_BOOSTER2));

        immunizationToIndicator.put(HEPATITIS_0_VALUE, asList(HEP));

        immunizationToIndicator.put(OPV_0_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_1_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_2_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_3_VALUE, asList(OPV, PENTAVALENT3_OR_OPV3));
        immunizationToIndicator.put(OPV_BOOSTER_VALUE, asList(OPV, DPT_BOOSTER_OR_OPV_BOOSTER));

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
        reportImmunizations(child, immunizations, location, child.dateOfBirth());
        reportBirthWeight(child, location);
        reportBFPostBirth(reportData.get(BF_POSTBIRTH), child, location);
        reportToBoth(child, INFANT_REGISTRATION, child.dateOfBirth(), location);
        reportToBoth(child, INFANT_BALANCE_TOTAL, child.dateOfBirth(), location);
    }

    public void immunizationProvided(SafeMap reportData, List<String> previousImmunizations) {
        Child child = allChildren.findByCaseId(reportData.get(ID));

        List<String> immunizations = new ArrayList<>(asList(reportData.get(IMMUNIZATIONS_GIVEN_FIELD_NAME).split(" ")));
        immunizations.removeAll(previousImmunizations);

        Location location = loadLocationOfChild(child);
        reportImmunizations(child, immunizations, location, reportData.get(IMMUNIZATION_DATE_FIELD_NAME));
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
        String vitaminDose = reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DOSE);
        if (VITAMIN_A_DOSES_1_2_5_9.contains(vitaminDose)) {
            reportToBoth(child, indicator, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
        }
    }

    public void pncVisitHappened(SafeMap reportData) {
        String id = reportData.get(CHILD_ID_FIELD);
        Child child = allChildren.findByCaseId(id);

        String problems = reportData.get(URINE_STOOL_PROBLEMS);
        if (!StringUtils.isBlank(problems) && problems.contains(AllConstants.CommonChildFormFields.DIARRHEA_VALUE)) {
            Location location = loadLocationOfChild(child);
            reportToBoth(child, CHILD_DIARRHEA, reportData.get(VISIT_DATE_FIELD_NAME), location);
        }
    }

    public void sickVisitHappened(SafeMap reportData) {
        String id = reportData.get(ID);
        Child child = allChildren.findByCaseId(id);

        Location location = loadLocationOfChild(child);
        LocalDate childDateOfBirth = parse(child.dateOfBirth());
        if (childDateOfBirth.plusYears(CHILD_DIARRHEA_THRESHOLD_IN_YEARS).isAfter(LocalDate.parse(reportData.get(AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME)))) {
            if (!StringUtils.isBlank(reportData.get(CHILD_SIGNS)) && reportData.get(CHILD_SIGNS).contains(AllConstants.CommonChildFormFields.DIARRHEA_VALUE)) {
                reportToBoth(child, CHILD_DIARRHEA, reportData.get(SICK_VISIT_DATE), location);
            } else if (!StringUtils.isBlank(reportData.get(REPORT_CHILD_DISEASE)) && reportData.get(REPORT_CHILD_DISEASE).contains(AllConstants.ChildIllnessFields.DIARRHEA_DEHYDRATION_VALUE)) {
                reportToBoth(child, CHILD_DIARRHEA, reportData.get(REPORT_CHILD_DISEASE_DATE), location);
            }
        }
    }

    public void closeChild(SafeMap reportData) {
        if (!DEATH_OF_CHILD_VALUE.equals(reportData.get(CLOSE_REASON_FIELD_NAME))) {
            return;
        }

        Child child = allChildren.findByCaseId(reportData.get(ID));
        Location location = loadLocationOfChild(child);
        LocalDate childDateOfBirth = parse(child.dateOfBirth());
        String diedOn = reportData.get(DATE_OF_DEATH_FIELD_NAME);
        LocalDate diedOnDate = parse(diedOn);

        if (childDateOfBirth.plusDays(CHILD_EARLY_NEONATAL_MORTALITY_THRESHOLD_IN_DAYS).isAfter(diedOnDate)) {
            reportToBoth(child, ENM, diedOn, location);
        }
        if (childDateOfBirth.plusDays(CHILD_NEONATAL_MORTALITY_THRESHOLD_IN_DAYS).isAfter(diedOnDate)) {
            reportToBoth(child, NM, diedOn, location);
            reportToBoth(child, INFANT_MORTALITY, diedOn, location);
        } else if (childDateOfBirth.plusYears(INFANT_MORTALITY_THRESHOLD_IN_YEARS).isAfter(diedOnDate)) {
            reportToBoth(child, LNM, diedOn, location);
            reportToBoth(child, INFANT_MORTALITY, diedOn, location);
        }
        if (childDateOfBirth.plusYears(CHILD_MORTALITY_THRESHOLD_IN_YEARS).isAfter(diedOnDate)) {
            reportToBoth(child, CHILD_MORTALITY, diedOn, location);
        } else {
            logger.warn("Not reporting for child with CaseID" + child.caseId() + "because child's age is more than " + CHILD_MORTALITY_THRESHOLD_IN_YEARS + " years.");
        }
        if (AllConstants.CommonChildFormFields.DIARRHEA_VALUE.equalsIgnoreCase(reportData.get(DEATH_CAUSE))) {
            reportToBoth(child, CHILD_MORTALITY_DUE_TO_DIARRHEA, diedOn, location);
        }
    }

    private void reportVitaminADose2ForMaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_2_VALUE.equals(reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_2, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
            reportToBoth(child, VIT_A_2_FOR_MALE_CHILD, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
        }
    }

    private void reportVitaminADose5ForMaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_5_VALUE.equals(reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_5_FOR_MALE_CHILD, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
        }
    }

    private void reportVitaminADose9ForMaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_9_VALUE.equals(reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_9_FOR_MALE_CHILD, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
        }
    }

    private void reportVitaminADose1ForMaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_1_VALUE.equals(reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_1, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
            reportToBoth(child, VIT_A_1_FOR_MALE_CHILD, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
        }
    }

    private void reportVitaminADose2ForFemaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_2_VALUE.equals(reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_2, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
            reportToBoth(child, VIT_A_2_FOR_FEMALE_CHILD, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
        }
    }

    private void reportVitaminADose5ForFemaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_5_VALUE.equals(reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_5_FOR_FEMALE_CHILD, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
        }
    }

    private void reportVitaminADose9ForFemaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_9_VALUE.equals(reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_9_FOR_FEMALE_CHILD, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
        }
    }

    private void reportVitaminADose1ForFemaleChild(SafeMap reportData, Child child, Location location) {
        if (VITAMIN_A_DOSE_1_VALUE.equals(reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DOSE))) {
            reportToBoth(child, VIT_A_1, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
            reportToBoth(child, VIT_A_1_FOR_FEMALE_CHILD, reportData.get(AllConstants.VitaminAFields.VITAMIN_A_DATE), location);
        }
    }

    private Location loadLocationOfChild(Child child) {
        Mother mother = allMothers.findByCaseId(child.motherCaseId());
        EligibleCouple couple = allEligibleCouples.findByCaseId(mother.ecCaseId());
        return new Location(couple.village(), couple.subCenter(), couple.phc());
    }

    private void reportBirthWeight(Child child, Location location) {
        try {
            double birthWeight = Double.parseDouble(child.weight());
            if (birthWeight < LOW_BIRTH_WEIGHT_THRESHOLD) {
                reportToBoth(child, LBW, child.dateOfBirth(), location);
            }
            reportToBoth(child, WEIGHED_AT_BIRTH, child.dateOfBirth(), location);
        } catch (NumberFormatException e) {
            logger.warn("Not reporting: Invalid value received for childWeight : " + child.weight() + " for childId : " + child.caseId());
        }
    }

    private void reportBFPostBirth(String bfPostBirth, Child child, Location location) {
        if (BOOLEAN_TRUE_VALUE.equalsIgnoreCase(bfPostBirth)) {
            reportToBoth(child, BF_POST_BIRTH, child.dateOfBirth(), location);
        }
    }

    private void reportImmunizations(Child child, List<String> immunizations, Location location, String date) {
        for (String immunizationProvidedThisTime : immunizations) {
            List<Indicator> indicators = immunizationToIndicator.get(immunizationProvidedThisTime);
            if (indicators == null) {
                logger.warn("Not reporting: Invalid immunization: " + immunizationProvidedThisTime + " for childCaseId: " +
                        child.caseId() + " with immunizations provided: " + immunizations);
                continue;
            }

            for (Indicator indicator : indicators) {
                reportToBoth(child, indicator, date, location);
            }
        }
    }

    public void reportToBoth(Child child, Indicator indicator, String date, Location location) {
        ReportingData serviceProvidedData = ReportingData.serviceProvidedData(child.anmIdentifier(), child.thayiCardNumber(), indicator, date, location);
        reportingService.sendReportData(serviceProvidedData);
        ReportingData anmReportData = ReportingData.anmReportData(child.anmIdentifier(), child.caseId(), indicator, date);
        reportingService.sendReportData(anmReportData);
    }

    public void reportInfantBalance() {
        reportInfantBalanceOnHand();
    }

    private void reportInfantBalanceOnHand() {
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

}
