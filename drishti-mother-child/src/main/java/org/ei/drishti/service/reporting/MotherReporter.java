package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.domain.Location;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.util.SafeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MotherReporter implements IReporter {

    private MotherReportingService motherReportingService;
    private AllMothers allMothers;

    @Autowired
    public MotherReporter(MotherReportingService motherReportingService, AllMothers allMothers) {
        this.motherReportingService = motherReportingService;
        this.allMothers = allMothers;
    }

    @Override
    public void report(String entityId, String reportIndicator, Location location, String serviceProvidedDate, SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(entityId);
        motherReportingService.reportToBoth(mother, Indicator.from(reportIndicator), serviceProvidedDate, location);
    }

}
