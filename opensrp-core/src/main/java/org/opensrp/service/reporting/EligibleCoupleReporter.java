package org.opensrp.service.reporting;

import static org.opensrp.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;

import org.opensrp.common.domain.Indicator;
import org.opensrp.domain.EligibleCouple;
import org.opensrp.domain.Location;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.util.SafeMap;
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
    public void report(String entityId, String reportIndicator, Location location, String serviceProvidedDate, SafeMap reportData) {
        EligibleCouple eligibleCouple = allEligibleCouples.findByCaseId(entityId);
        //#TODO: Pull out the reportIndicator method out of ecReportingService
        ecReportingService.reportIndicator(reportData, eligibleCouple, Indicator.from(reportIndicator), serviceProvidedDate, reportData.get(SUBMISSION_DATE_FIELD_NAME));
    }

}
