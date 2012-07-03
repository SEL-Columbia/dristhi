package org.ei.drishti.service;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.domain.Child;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChildReportingService {
    private final ReportingService reportingService;
    private final AllChildren allChildren;
    private static Logger logger = LoggerFactory.getLogger(ChildReportingService.class.toString());

    @Autowired
    public ChildReportingService(ReportingService reportingService, AllChildren allChildren) {
        this.reportingService = reportingService;
        this.allChildren = allChildren;
    }

    public void updateChildImmunization(ChildImmunizationUpdationRequest updationRequest, Map<String, String> reportingData) {
        SafeMap safeReportingData = new SafeMap(reportingData);

        Child child = allChildren.findByCaseId(updationRequest.caseId());
        if (child == null) {
            logger.warn("Child Case not found for child with CaseID " + updationRequest.caseId());
            return;
        }

        List<String> immunizations = updationRequest.immunizationsProvided();
        List<String> previouslyProvided = child.immunizationsProvided();
        immunizations.removeAll(previouslyProvided);

        for (String immunizationProvidedThisTime : immunizations) {
            ReportingData data = ReportingData.updateChildImmunization(safeReportingData.get("anmIdentifier"), child.thaayiCardNumber(),
                    immunizationProvidedThisTime, reportingData.get("immunizationsProvidedDate"), child.location());

            reportingService.sendReportData(data);
        }
    }
}
