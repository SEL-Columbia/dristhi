package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;

@Component
public class AgeIsLessThanOneYearRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate dateOfBirth = LocalDate.parse(reportFields.get(AllConstants.ChildRegistrationFormFields.DATE_OF_BIRTH));
        LocalDate submissionDate = LocalDate.parse(reportFields.get(SUBMISSION_DATE_FIELD_NAME));

        return dateOfBirth.plusYears(1).isAfter(submissionDate);
    }
}
