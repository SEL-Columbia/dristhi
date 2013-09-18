package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.util.IntegerUtil;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.reporting.ReferenceData;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME;

@Component
public class MoreThanZeroCondomsSuppliedRule implements IRule {
    @Override
    public boolean apply(FormSubmission submission, List<String> formFields, ReferenceData referenceData) {
        return IntegerUtil.tryParse(submission.getField(NUMBER_OF_CONDOMS_SUPPLIED_FIELD_NAME), 0) > 0;
    }
}

