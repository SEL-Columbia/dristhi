package org.ei.drishti.service.reporting;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Location;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllChildren;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void report(FormSubmission submission, String reportIndicator, Location location) {
        Child child = allChildren.findByCaseId(submission.entityId());
        childReportingService.reportToBoth(child, Indicator.from(reportIndicator),
                            submission.getField(AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME), location);
    }
}
