package org.ei.drishti.service;

import org.apache.commons.lang.StringUtils;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.domain.Child;
import org.ei.drishti.repository.AllChildren;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<String> provided = updationRequest.immunizationsProvided();

        Child child = allChildren.findByCaseId(updationRequest.caseId());
        if (child == null) {
            logger.warn("Child Case not found for child with CaseID " + updationRequest.caseId());
            return;
        }
        List<String> previouslyProvided = child.immunizationsProvided();
        provided.removeAll(previouslyProvided);

        reportingData.put("immunizationsProvided", StringUtils.join(provided.toArray(new String[0])));
        reportingData.put("thaayiCardNumber", child.thaayiCardNumber());
        reportingService.sendReportData(new ReportingData("updateChildImmunization", reportingData));
    }
}
