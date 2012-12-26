package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.common.util.DateUtil;
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
import static org.ei.drishti.common.AllConstants.ChildBirthCommCareFields.BF_POSTBIRTH_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ChildBirthCommCareFields.BIRTH_WEIGHT_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ChildBirthCommCareFields.YES_BF_POSTBIRTH_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ChildCloseCommCareFields.CLOSE_REASON_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ChildCloseCommCareFields.DEATH_OF_CHILD_COMMCARE_VALUE;
import static org.ei.drishti.common.AllConstants.ChildImmunizationCommCareFields.*;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.CASE_ID_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.SUBMISSION_DATE_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.Report.LOW_BIRTH_WEIGHT_THRESHOLD;
import static org.ei.drishti.common.domain.Indicator.*;

@Service
public class ChildReportingService {
    public static final int CHILD_MORTALITY_REPORTING_THRESHOLD_IN_MONTHS = 11;
    private final ReportingService reportingService;
    private final AllChildren allChildren;
    private static Logger logger = LoggerFactory.getLogger(ChildReportingService.class.toString());
    private Map<String, Indicator> immunizationToIndicator;

    @Autowired
    public ChildReportingService(ReportingService reportingService, AllChildren allChildren) {
        this.reportingService = reportingService;
        this.allChildren = allChildren;
        immunizationToIndicator = new HashMap<>();

        immunizationToIndicator.put(BCG_COMMCARE_VALUE, BCG);

        immunizationToIndicator.put(DPT_1_COMMCARE_VALUE, DPT1);
        immunizationToIndicator.put(DPT_2_COMMCARE_VALUE, DPT2);
        immunizationToIndicator.put(DPT_3_COMMCARE_VALUE, DPT3);
        immunizationToIndicator.put(DPT_BOOSTER_1_COMMCARE_VALUE, DPT);
        immunizationToIndicator.put(DPT_BOOSTER_2_COMMCARE_VALUE, DPT_BOOSTER2);

        immunizationToIndicator.put(HEPATITIS_0_COMMCARE_VALUE, HEP);
        immunizationToIndicator.put(HEPATITIS_1_COMMCARE_VALUE, HEP);
        immunizationToIndicator.put(HEPATITIS_2_COMMCARE_VALUE, HEP);
        immunizationToIndicator.put(HEPATITIS_3_COMMCARE_VALUE, HEP);

        immunizationToIndicator.put(OPV_0_COMMCARE_VALUE, OPV);
        immunizationToIndicator.put(OPV_1_COMMCARE_VALUE, OPV);
        immunizationToIndicator.put(OPV_2_COMMCARE_VALUE, OPV);
        immunizationToIndicator.put(OPV_3_COMMCARE_VALUE, OPV);
        immunizationToIndicator.put(OPV_BOOSTER_COMMCARE_VALUE, OPV);

        immunizationToIndicator.put(MEASLES_COMMCARE_VALUE, MEASLES);
        immunizationToIndicator.put(MEASLES_BOOSTER_COMMCARE_VALUE, MEASLES);
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
    }

    public void closeChild(SafeMap reportData) {
        if (!DEATH_OF_CHILD_COMMCARE_VALUE.equals(reportData.get(CLOSE_REASON_COMMCARE_FIELD_NAME))) {
            return;
        }

        Child child = allChildren.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));
        LocalDate childDateOfBirth = LocalDate.parse(child.dateOfBirth());
        if ((childDateOfBirth.plusMonths(CHILD_MORTALITY_REPORTING_THRESHOLD_IN_MONTHS).isBefore(DateUtil.today()))) {
            logger.warn("Not reporting for child because child's age is more than " + CHILD_MORTALITY_REPORTING_THRESHOLD_IN_MONTHS + " months.");
            return;
        }

        reportToBoth(child, CHILD_MORTALITY, reportData.get(SUBMISSION_DATE_COMMCARE_FIELD_NAME));
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
        if (YES_BF_POSTBIRTH_COMMCARE_FIELD_NAME.equals(bfPostBirth)) {
            reportToBoth(child, BF_POST_BIRTH, child.dateOfBirth());
        }
    }

    private void reportImmunizations(String caseId, Child child, List<String> immunizations, String date) {
        for (String immunizationProvidedThisTime : immunizations) {
            Indicator indicator = immunizationToIndicator.get(immunizationProvidedThisTime);
            if (indicator == null) {
                logger.warn("Not reporting: Invalid immunization: " + immunizationProvidedThisTime + " for childCaseId: " +
                        caseId + " with immunizations provided: " + immunizations);
                continue;
            }

            reportToBoth(child, indicator, date);
        }
    }

    private void reportToBoth(Child child, Indicator indicator, String date) {
        ReportingData serviceProvidedData = ReportingData.serviceProvidedData(child.anmIdentifier(), child.thaayiCardNumber(), indicator, date, child.location());
        reportingService.sendReportData(serviceProvidedData);
        ReportingData anmReportData = ReportingData.anmReportData(child.anmIdentifier(), child.caseId(), indicator, date);
        reportingService.sendReportData(anmReportData);
    }
}
