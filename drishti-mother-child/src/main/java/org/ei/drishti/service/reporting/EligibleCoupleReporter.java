package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.util.SafeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EligibleCoupleReporter implements IReporter {

    private ECReportingService ecReportingService;
    private AllEligibleCouples allEligibleCouples;

    @Autowired
    public EligibleCoupleReporter(ECReportingService ecReportingService, AllEligibleCouples allEligibleCouples) {
        this.ecReportingService = ecReportingService;
        this.allEligibleCouples = allEligibleCouples;
    }

    @Override
    public void report(String entityId, String reportIndicator, Location location, SafeMap reportData) {
        EligibleCouple eligibleCouple = allEligibleCouples.findByCaseId(entityId);
        ecReportingService.reportIndicator(reportData, eligibleCouple, Indicator.from(reportIndicator));
    }
}
