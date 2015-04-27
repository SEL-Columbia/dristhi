package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ChildIllnessFields.REPORT_CHILD_DISEASE_DATE;
import static org.opensrp.common.AllConstants.ChildRegistrationFormFields.DATE_OF_BIRTH;

import org.joda.time.LocalDate;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsChildBelowFiveYearsRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate dateOfBirth = LocalDate.parse(reportFields.get(DATE_OF_BIRTH));
        LocalDate reportDate = LocalDate.parse(reportFields.get(REPORT_CHILD_DISEASE_DATE));
        return dateOfBirth.plusYears(5).isAfter(reportDate);
    }
}