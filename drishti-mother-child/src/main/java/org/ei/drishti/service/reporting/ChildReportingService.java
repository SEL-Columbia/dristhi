package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.drishti.common.AllConstants.ChildImmunizationCommCareValues.*;
import static org.ei.drishti.common.domain.Indicator.*;

@Service
public class ChildReportingService {
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

        immunizationToIndicator.put(DPT_0_COMMCARE_VALUE, DPT);
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
        Child child = allChildren.findByCaseId(updationRequest.caseId());
        if (child == null) {
            logger.warn("Child Case not found for child with CaseID " + updationRequest.caseId());
            return;
        }

        EligibleCouple couple = allEligibleCouples.findByCaseId(child.ecCaseId());
        if (couple == null) {
            logger.warn("EC Case not found for child with CaseID " + child.ecCaseId());
            return;
        }

        List<String> immunizations = updationRequest.immunizationsProvidedList();
        List<String> previouslyProvided = child.immunizationsProvided();
        immunizations.removeAll(previouslyProvided);

        for (String immunizationProvidedThisTime : immunizations) {
            Indicator indicator = immunizationToIndicator.get(immunizationProvidedThisTime);
            if (indicator == null) {
                logger.warn("Not reporting: Invalid immunization: " + immunizationProvidedThisTime + " in " +
                        updationRequest + " with reporting data: " + reportingData);
                continue;
            }

            ReportingData serviceProvidedData = ReportingData.serviceProvidedData(reportingData.get("anmIdentifier"), child.thaayiCardNumber(),
                    indicator, reportingData.get("immunizationsProvidedDate"), couple.location());
            reportingService.sendReportData(serviceProvidedData);

            ReportingData anmReportData = ReportingData.anmReportData(reportingData.get("anmIdentifier"), child.caseId(),
                    indicator, reportingData.get("immunizationsProvidedDate"));
            reportingService.sendReportData(anmReportData);
        }
    }
}
