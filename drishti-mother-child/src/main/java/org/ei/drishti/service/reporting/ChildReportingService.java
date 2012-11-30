package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.ChildInformation;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.drishti.common.AllConstants.ChildCloseCommCareFields.*;
import static org.ei.drishti.common.AllConstants.ChildImmunizationCommCareValues.*;
import static org.ei.drishti.common.AllConstants.CommonCommCareFields.*;
import static org.ei.drishti.common.AllConstants.MetaCommCareFields.*;
import static org.ei.drishti.common.domain.Indicator.*;

@Service
public class ChildReportingService {
    public static final int CHILD_MORTALITY_REPORTING_THRESHOLD_IN_MONTHS = 11;
    private final ReportingService reportingService;
    private final AllChildren allChildren;
    private AllEligibleCouples allEligibleCouples;
    private static Logger logger = LoggerFactory.getLogger(ChildReportingService.class.toString());
    private Map<String, Indicator> immunizationToIndicator;

    @Autowired
    public ChildReportingService(ReportingService reportingService, AllChildren allChildren, AllEligibleCouples allEligibleCouples) {
        this.reportingService = reportingService;
        this.allChildren = allChildren;
        this.allEligibleCouples = allEligibleCouples;
        immunizationToIndicator = new HashMap<>();

        immunizationToIndicator.put(BCG_COMMCARE_VALUE, BCG);

        immunizationToIndicator.put(DPT_1_COMMCARE_VALUE, DPT);
        immunizationToIndicator.put(DPT_2_COMMCARE_VALUE, DPT);
        immunizationToIndicator.put(DPT_3_COMMCARE_VALUE, DPT);
        immunizationToIndicator.put(DPT_BOOSTER_1_COMMCARE_VALUE, DPT);
        immunizationToIndicator.put(DPT_BOOSTER_2_COMMCARE_VALUE, DPT);

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

    public void updateChildImmunization(ChildImmunizationUpdationRequest updationRequest, SafeMap reportingData) {
        report(updationRequest.caseId(), updationRequest.immunizationsProvidedList(), reportingData.get("immunizationsProvidedDate"), reportingData.get("anmIdentifier"));
    }

    public void updateChildImmunization(ChildInformation childInformation) {
        report(childInformation.caseId(), childInformation.immunizationsProvidedList(), childInformation.dateOfBirth().toString(), childInformation.anmIdentifier());
    }

    private void report(String childCaseId, List<String> immunizationsProvided, String immunizationsProvidedDate, String anmIdentifier) {
        Child child = allChildren.findByCaseId(childCaseId);
        if (child == null) {
            logChildNotFound(childCaseId);
            return;
        }

        EligibleCouple couple = allEligibleCouples.findByCaseId(child.ecCaseId());
        if (couple == null) {
            logECNotFound(child);
            return;
        }

        List<String> immunizations = immunizationsProvided;
        List<String> previouslyProvided = child.immunizationsProvided();
        immunizations.removeAll(previouslyProvided);

        for (String immunizationProvidedThisTime : immunizations) {
            Indicator indicator = immunizationToIndicator.get(immunizationProvidedThisTime);
            if (indicator == null) {
                logger.warn("Not reporting: Invalid immunization: " + immunizationProvidedThisTime + " for childCaseId: " +
                        childCaseId + " with immunizations provided: " + immunizationsProvided);
                continue;
            }

            ReportingData serviceProvidedData = ReportingData.serviceProvidedData(anmIdentifier, child.thaayiCardNumber(),
                    indicator, immunizationsProvidedDate, couple.location());
            reportingService.sendReportData(serviceProvidedData);

            ReportingData anmReportData = ReportingData.anmReportData(anmIdentifier, child.caseId(),
                    indicator, immunizationsProvidedDate);
            reportingService.sendReportData(anmReportData);
        }
    }

    public void closeChild(Map<String, String> reportData) {
        if (!reportData.get(CLOSE_REASON_COMMCARE_FIELD_NAME).equals(DEATH_OF_CHILD_COMMCARE_VALUE)) {
            return;
        }

        Child child = allChildren.findByCaseId(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));
        if (child == null) {
            logChildNotFound(reportData.get(CASE_ID_COMMCARE_FIELD_NAME));
            return;
        }

        EligibleCouple couple = allEligibleCouples.findByCaseId(child.ecCaseId());
        if (couple == null) {
            logECNotFound(child);
            return;
        }

        LocalDate childDateOfBirth = LocalDate.parse(child.dateOfBirth());
        if((childDateOfBirth.plusMonths(CHILD_MORTALITY_REPORTING_THRESHOLD_IN_MONTHS).isBefore(DateUtil.today()))){
            logger.warn("Not reporting for child because child's age is more than " + CHILD_MORTALITY_REPORTING_THRESHOLD_IN_MONTHS + " months.");
            return;
        }

        ReportingData serviceProvided = ReportingData.serviceProvidedData(reportData.get(ANM_IDENTIFIER_COMMCARE_FIELD_NAME), child.thaayiCardNumber(), CHILD_MORTALITY,
                reportData.get(SUBMISSION_DATE_COMMCARE_FIELD_NAME), couple.location());
        reportingService.sendReportData(serviceProvided);

        ReportingData anmReportData = ReportingData.anmReportData(reportData.get("anmIdentifier"), child.caseId(), CHILD_MORTALITY, reportData.get("submissionDate"));
        reportingService.sendReportData(anmReportData);
    }

    private void logChildNotFound(String childCaseId) {
        logger.warn("Child Case not found for child with CaseID " + childCaseId);
    }

    private void logECNotFound(Child child) {
        logger.warn("EC Case not found for child with CaseID " + child.caseId() + " ec CaseID : " + child.ecCaseId());
    }
}
