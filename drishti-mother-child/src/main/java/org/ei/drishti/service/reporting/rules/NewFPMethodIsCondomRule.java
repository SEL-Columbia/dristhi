package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.reporting.ReferenceData;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.CONDOM_FP_METHOD_VALUE;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.NEW_FP_METHOD_FIELD_NAME;

@Component
public class NewFPMethodIsCondomRule implements IRule {

    @Override
    public boolean apply(FormSubmission submission, List<String> formFields, ReferenceData referenceData) {
        return CONDOM_FP_METHOD_VALUE.equalsIgnoreCase(submission.getField(NEW_FP_METHOD_FIELD_NAME));
    }
}

