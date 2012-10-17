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

        immunizationToIndicator.put("bcg", BCG);

        immunizationToIndicator.put("dpt_1", DPT);
        immunizationToIndicator.put("dpt_2", DPT);
        immunizationToIndicator.put("dpt_3", DPT);
        immunizationToIndicator.put("dptbooster_1", DPT);
        immunizationToIndicator.put("dptbooster_2", DPT);

        immunizationToIndicator.put("hepB_0", HEP);
        immunizationToIndicator.put("hepb_1", HEP);
        immunizationToIndicator.put("hepb_2", HEP);
        immunizationToIndicator.put("hepb_3", HEP);

        immunizationToIndicator.put("opv_0", OPV);
        immunizationToIndicator.put("opv_1", OPV);
        immunizationToIndicator.put("opv_2", OPV);
        immunizationToIndicator.put("opvbooster", OPV);

        immunizationToIndicator.put("measles", MEASLES);
        immunizationToIndicator.put("MeaslesBooster", MEASLES);
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

        List<String> immunizations = updationRequest.immunizationsProvided();
        List<String> previouslyProvided = child.immunizationsProvided();
        immunizations.removeAll(previouslyProvided);

        for (String immunizationProvidedThisTime : immunizations) {
            Indicator indicator = immunizationToIndicator.get(immunizationProvidedThisTime);
            if (indicator == null) {
                logger.warn("Not reporting: Invalid immunization: " + immunizationProvidedThisTime + " in " +
                        updationRequest + " with reporting data: " + reportingData);
                continue;
            }

            ReportingData data = ReportingData.serviceProvidedData(reportingData.get("anmIdentifier"), child.thaayiCardNumber(),
                    indicator, reportingData.get("immunizationsProvidedDate"), couple.location());

            reportingService.sendReportData(data);
        }
    }
}
