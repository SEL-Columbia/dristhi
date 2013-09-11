package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.util.SafeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static org.ei.drishti.common.AllConstants.CommonFormFields.ID;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.FP_METHOD_CHANGE_DATE_FIELD_NAME;

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
    public void report(FormSubmission submission, String reportIndicator, Location location) {
        ArrayList<String> fieldNames = new ArrayList<>();
        fieldNames.add(FP_METHOD_CHANGE_DATE_FIELD_NAME);
        fieldNames.add(ID);
        SafeMap reportData = new SafeMap(submission.getFields(fieldNames));
        EligibleCouple eligibleCouple = allEligibleCouples.findByCaseId(submission.entityId());
        ecReportingService.reportIndicator(reportData, eligibleCouple, Indicator.from(reportIndicator));
    }
}
