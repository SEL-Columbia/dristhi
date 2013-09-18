package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.util.SafeMap;
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
    public void report(String entityId, String reportIndicator, Location location, String serviceProvidedDate, SafeMap reportData) {
        Child child = allChildren.findByCaseId(entityId);
        childReportingService.reportToBoth(child, Indicator.from(reportIndicator), serviceProvidedDate, location);
    }
}
