package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.domain.Child;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.service.reporting.ReferenceData;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.ei.drishti.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;

@Component
public class AgeIsLessThanOneYearRule implements IRule {

    private AllChildren allChildren;

    @Autowired
    public AgeIsLessThanOneYearRule(AllChildren allChildren) {
        this.allChildren = allChildren;
    }

    @Override
    public boolean apply(FormSubmission submission, List<String> formFields, ReferenceData referenceData) {
        Child child = allChildren.findByCaseId(submission.entityId());
        LocalDate dateOfBirth = LocalDate.parse(child.dateOfBirth());
        LocalDate submissionDate = LocalDate.parse(submission.getField(SUBMISSION_DATE_FIELD_NAME));

        return dateOfBirth.plusYears(1).isAfter(submissionDate);
    }
}
