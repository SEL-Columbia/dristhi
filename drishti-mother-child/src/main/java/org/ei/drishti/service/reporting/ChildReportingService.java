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
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ChildBirthCommCareFields.BF_POSTBIRTH_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ChildCloseFields.*;
import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.*;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.CASE_ID_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;
import static org.ei.drishti.common.AllConstants.Form.ID;
import static org.ei.drishti.common.AllConstants.Report.*;
import static org.ei.drishti.common.domain.Indicator.*;
import static org.joda.time.LocalDate.parse;

@Service
public class ChildReportingService {
    private static Logger logger = LoggerFactory.getLogger(ChildReportingService.class.toString());
    private final ReportingService reportingService;
    private final AllChildren allChildren;
    private final AllMothers allMothers;
    private final AllEligibleCouples allEligibleCouples;
    private Map<String, List<Indicator>> immunizationToIndicator;

    @Autowired
    public ChildReportingService(ReportingService reportingService, AllChildren allChildren, AllMothers allMothers, AllEligibleCouples allEligibleCouples) {
        this.reportingService = reportingService;
        this.allChildren = allChildren;
        this.allMothers = allMothers;
        this.allEligibleCouples = allEligibleCouples;
        immunizationToIndicator = new HashMap<>();

        immunizationToIndicator.put(BCG_VALUE, asList(BCG));

        immunizationToIndicator.put(DPT_BOOSTER_1_VALUE, asList(DPT, DPT_BOOSTER_OR_OPV_BOOSTER));
        immunizationToIndicator.put(DPT_BOOSTER_2_VALUE, asList(DPT_BOOSTER2));

        immunizationToIndicator.put(HEPATITIS_0_VALUE, asList(HEP));

        immunizationToIndicator.put(OPV_0_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_1_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_2_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_3_VALUE, asList(OPV, DPT3_OR_OPV3));
        immunizationToIndicator.put(OPV_BOOSTER_VALUE, asList(OPV, DPT_BOOSTER_OR_OPV_BOOSTER));

        immunizationToIndicator.put(MEASLES_VALUE, asList(MEASLES));

        immunizationToIndicator.put(PENTAVALENT_1_VALUE, asList(PENT1));
        immunizationToIndicator.put(PENTAVALENT_2_VALUE, asList(PENT2));
        immunizationToIndicator.put(PENTAVALENT_3_VALUE, asList(PENT3));

        immunizationToIndicator.put(MMR_VALUE, asList(MMR));
        immunizationToIndicator.put(JE_VALUE, asList(JE));
    }

    public void registerChild(SafeMap reportData) {
        String id = reportData.get(ID);
        Child child = allChildren.findByCaseId(id);

        List<String> immunizations = child.immunizationsGiven();

        Location location = loadLocationOfChild(child);
        reportImmunizations(child, immunizations, location, child.dateOfBirth());
        reportBirthWeight(child, location);
        reportBFPostBirth(reportData.get(BF_POSTBIRTH_FIELD_NAME), child, location);
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

        if ("1".equals(reportData.get(VITAMIN_A_DOSE_COMMCARE_FIELD_NAME))) {
            reportToBoth(child, VIT_A_1, reportData.get(IMMUNIZATION_DATE_FIELD_NAME));
        } else if ("2".equals(reportData.get(VITAMIN_A_DOSE_COMMCARE_FIELD_NAME))) {
            reportToBoth(child, VIT_A_2, reportData.get(IMMUNIZATION_DATE_FIELD_NAME));
        }
    }

    private Location loadLocationOfChild(Child child) {
        Mother mother = allMothers.findByCaseId(child.motherCaseId());
        EligibleCouple couple = allEligibleCouples.findByCaseId(mother.ecCaseId());
        return new Location(couple.village(), couple.subCenter(), couple.phc());
    }

    public void closeChild(SafeMap reportData) {
        if (!DEATH_OF_CHILD_VALUE.equals(reportData.get(CLOSE_REASON_FIELD_NAME))) {
            return;
        }

        Child child = allChildren.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));
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

    private void reportToBoth(Child child, Indicator indicator, String date) {
        ReportingData serviceProvidedData = ReportingData.serviceProvidedData(child.anmIdentifier(), child.thayiCardNumber(), indicator, date, child.location());
        reportingService.sendReportData(serviceProvidedData);
        ReportingData anmReportData = ReportingData.anmReportData(child.anmIdentifier(), child.caseId(), indicator, date);
        reportingService.sendReportData(anmReportData);
    }

    private void reportToBoth(Child child, Indicator indicator, String date, Location location) {
        ReportingData serviceProvidedData = ReportingData.serviceProvidedData(child.anmIdentifier(), child.thayiCardNumber(), indicator, date, location);
        reportingService.sendReportData(serviceProvidedData);
        ReportingData anmReportData = ReportingData.anmReportData(child.anmIdentifier(), child.caseId(), indicator, date);
        reportingService.sendReportData(anmReportData);
    }
}
