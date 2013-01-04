package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Child;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ChildBirthCommCareFields.*;
import static org.ei.drishti.common.AllConstants.ChildCloseCommCareFields.*;
import static org.ei.drishti.common.AllConstants.ChildImmunizationCommCareFields.*;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.BOOLEAN_TRUE_COMMCARE_VALUE;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.CASE_ID_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.Report.*;
import static org.ei.drishti.common.domain.Indicator.*;
import static org.joda.time.LocalDate.parse;

@Service
public class ChildReportingService {
    private final ReportingService reportingService;
    private final AllChildren allChildren;
    private static Logger logger = LoggerFactory.getLogger(ChildReportingService.class.toString());
    private Map<String, List<Indicator>> immunizationToIndicator;

    @Autowired
    public ChildReportingService(ReportingService reportingService, AllChildren allChildren) {
        this.reportingService = reportingService;
        this.allChildren = allChildren;
        immunizationToIndicator = new HashMap<>();

        immunizationToIndicator.put(BCG_COMMCARE_VALUE, asList(BCG));

        immunizationToIndicator.put(DPT_1_COMMCARE_VALUE, asList(DPT1));
        immunizationToIndicator.put(DPT_2_COMMCARE_VALUE, asList(DPT2));
        immunizationToIndicator.put(DPT_3_COMMCARE_VALUE, asList(DPT3, DPT3_OR_OPV3));
        immunizationToIndicator.put(DPT_BOOSTER_1_COMMCARE_VALUE, asList(DPT, DPT_BOOSTER_OR_OPV_BOOSTER));
        immunizationToIndicator.put(DPT_BOOSTER_2_COMMCARE_VALUE, asList(DPT_BOOSTER2));

        immunizationToIndicator.put(HEPATITIS_0_COMMCARE_VALUE, asList(HEP));
        immunizationToIndicator.put(HEPATITIS_1_COMMCARE_VALUE, asList(HEP));
        immunizationToIndicator.put(HEPATITIS_2_COMMCARE_VALUE, asList(HEP));
        immunizationToIndicator.put(HEPATITIS_3_COMMCARE_VALUE, asList(HEP));

        immunizationToIndicator.put(OPV_0_COMMCARE_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_1_COMMCARE_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_2_COMMCARE_VALUE, asList(OPV));
        immunizationToIndicator.put(OPV_3_COMMCARE_VALUE, asList(OPV, DPT3_OR_OPV3));
        immunizationToIndicator.put(OPV_BOOSTER_COMMCARE_VALUE, asList(OPV, DPT_BOOSTER_OR_OPV_BOOSTER));

        immunizationToIndicator.put(MEASLES_COMMCARE_VALUE, asList(MEASLES));
    }

    public void registerChild(SafeMap reportData) {
        String caseId = reportData.get(CASE_ID_COMMCARE_FIELD_NAME);
        Child child = allChildren.findByCaseId(caseId);

        List<String> immunizations = child.immunizationsProvided();

        reportImmunizations(caseId, child, immunizations, child.dateOfBirth());
        reportBirthWeight(reportData.get(BIRTH_WEIGHT_COMMCARE_FIELD_NAME), child);
        reportBFPostBirth(reportData.get(BF_POSTBIRTH_COMMCARE_FIELD_NAME), child);
    }

    public void immunizationProvided(SafeMap reportData, Collection<String> previousImmunizations) {
        String caseId = reportData.get(CASE_ID_COMMCARE_FIELD_NAME);
        Child child = allChildren.findByCaseId(caseId);

        List<String> immunizations = new ArrayList<>(asList(reportData.get(IMMUNIZATIONS_PROVIDED_COMMCARE_FIELD_NAME).split(" ")));
        immunizations.removeAll(previousImmunizations);

        reportImmunizations(caseId, child, immunizations, reportData.get(IMMUNIZATIONS_PROVIDED_DATE_COMMCARE_FIELD_NAME));

        if ("1".equals(reportData.get(VITAMIN_A_DOSE_COMMCARE_FIELD_NAME))) {
            reportToBoth(child, VIT_A_1, reportData.get(IMMUNIZATIONS_PROVIDED_DATE_COMMCARE_FIELD_NAME));
        } else if ("2".equals(reportData.get(VITAMIN_A_DOSE_COMMCARE_FIELD_NAME))) {
            reportToBoth(child, VIT_A_2, reportData.get(IMMUNIZATIONS_PROVIDED_DATE_COMMCARE_FIELD_NAME));
        }
    }

    public void closeChild(SafeMap reportData) {
        if (!DEATH_OF_CHILD_COMMCARE_VALUE.equals(reportData.get(CLOSE_REASON_COMMCARE_FIELD_NAME))) {
            return;
        }

        Child child = allChildren.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));
        LocalDate childDateOfBirth = parse(child.dateOfBirth());
        String diedOn = reportData.get(DATE_OF_DEATH_COMMCARE_FIELD_NAME);
        LocalDate diedOnDate = parse(diedOn);

        if (childDateOfBirth.plusDays(CHILD_EARLY_NEONATAL_MORTALITY_THRESHOLD_IN_DAYS).isAfter(diedOnDate)) {
            reportToBoth(child, ENM, diedOn);
        }
        if (childDateOfBirth.plusDays(CHILD_NEONATAL_MORTALITY_THRESHOLD_IN_DAYS).isAfter(diedOnDate)) {
            reportToBoth(child, NM, diedOn);
            reportToBoth(child, INFANT_MORTALITY, diedOn);
        } else if (childDateOfBirth.plusYears(INFANT_MORTALITY_THRESHOLD_IN_YEARS).isAfter(diedOnDate)) {
            reportToBoth(child, LNM, diedOn);
            reportToBoth(child, INFANT_MORTALITY, diedOn);
        }
        if (childDateOfBirth.plusYears(CHILD_MORTALITY_THRESHOLD_IN_YEARS).isAfter(diedOnDate)) {
            reportToBoth(child, CHILD_MORTALITY, diedOn);
        } else {
            logger.warn("Not reporting for child with CaseID" + child.caseId() + "because child's age is more than " + CHILD_MORTALITY_THRESHOLD_IN_YEARS + " years.");
        }
    }

    private void reportBirthWeight(String weight, Child child) {
        try {
            double birthWeight = Double.parseDouble(weight);
            if (birthWeight < LOW_BIRTH_WEIGHT_THRESHOLD) {
                reportToBoth(child, LBW, child.dateOfBirth());
            }
            reportToBoth(child, WEIGHED_AT_BIRTH, child.dateOfBirth());
        } catch (NumberFormatException e) {
            logger.warn("Not reporting: Invalid value received for childWeight : " + weight + " for childCaseId : " + child.caseId());
        }
    }

    private void reportBFPostBirth(String bfPostBirth, Child child) {
        if (BOOLEAN_TRUE_COMMCARE_VALUE.equalsIgnoreCase(bfPostBirth)) {
            reportToBoth(child, BF_POST_BIRTH, child.dateOfBirth());
        }
    }

    private void reportImmunizations(String caseId, Child child, List<String> immunizations, String date) {
        for (String immunizationProvidedThisTime : immunizations) {
            List<Indicator> indicators = immunizationToIndicator.get(immunizationProvidedThisTime);
            if (indicators == null) {
                logger.warn("Not reporting: Invalid immunization: " + immunizationProvidedThisTime + " for childCaseId: " +
                        caseId + " with immunizations provided: " + immunizations);
                continue;
            }

            for (Indicator indicator : indicators) {
                reportToBoth(child, indicator, date);
            }
        }
    }

    private void reportToBoth(Child child, Indicator indicator, String date) {
        ReportingData serviceProvidedData = ReportingData.serviceProvidedData(child.anmIdentifier(), child.thaayiCardNumber(), indicator, date, child.location());
        reportingService.sendReportData(serviceProvidedData);
        ReportingData anmReportData = ReportingData.anmReportData(child.anmIdentifier(), child.caseId(), indicator, date);
        reportingService.sendReportData(anmReportData);
    }
}
