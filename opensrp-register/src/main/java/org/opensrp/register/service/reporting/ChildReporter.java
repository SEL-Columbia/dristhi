package org.opensrp.register.service.reporting;

import org.opensrp.common.domain.Indicator;
import org.opensrp.common.domain.Location;
import org.opensrp.register.domain.Child;
import org.opensrp.util.SafeMap;
import org.opensrp.register.repository.AllChildren;
import org.opensrp.service.reporting.IReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;

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
    public void report(String entityId, String reportIndicator, Location location, String serviceProvidedDate, SafeMap reportData) {
        Child child = allChildren.findByCaseId(entityId);
        childReportingService.reportToBoth(child, Indicator.from(reportIndicator), serviceProvidedDate, reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
    }
}
