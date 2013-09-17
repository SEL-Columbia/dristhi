package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.reporting.ReferenceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.CONDOM_FP_METHOD_VALUE;

@Component
public class CurrentFPMethodIsCondomRule implements IRule {

    private AllEligibleCouples allEligibleCouples;

    @Autowired
    public CurrentFPMethodIsCondomRule(AllEligibleCouples allEligibleCouples) {
        this.allEligibleCouples = allEligibleCouples;
    }

    @Override
    public boolean apply(FormSubmission submission, List<String> formFields, ReferenceData referenceData) {
        EligibleCouple eligibleCouple = allEligibleCouples.findByCaseId(submission.entityId());

        return CONDOM_FP_METHOD_VALUE.equalsIgnoreCase(eligibleCouple.currentMethod());
    }
}

