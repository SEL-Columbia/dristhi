package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.util.SafeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;

@Component
public class ChildReporter implements IReporter {

    private ChildReportingService childReportingService;
    private AllChildren allChildren;

    @Autowired
    public ChildReporter(ChildReportingService childReportingService, AllChildren allChildren) {
        this.childReportingService = childReportingService;
        this.allChildren = allChildren;
    }

    @Override
    public void report(String entityId, String reportIndicator, Location location, SafeMap reportData) {
        Child child = allChildren.findByCaseId(entityId);
        String submissionDate = reportData.get(SUBMISSION_DATE_FIELD_NAME);
        childReportingService.reportToBoth(child, Indicator.from(reportIndicator), submissionDate, location);
    }
}
