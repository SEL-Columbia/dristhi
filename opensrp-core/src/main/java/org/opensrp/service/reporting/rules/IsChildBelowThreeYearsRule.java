package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildRegistrationFormFields.DATE_OF_BIRTH;
import static org.opensrp.common.AllConstants.VitaminAFields.VITAMIN_A_DATE;

@Component
public class IsChildBelowThreeYearsRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate dateOfBirth = LocalDate.parse(reportFields.get(DATE_OF_BIRTH));
        LocalDate reportDate = LocalDate.parse(reportFields.get(VITAMIN_A_DATE));
        return dateOfBirth.plusYears(3).isAfter(reportDate);
    }
}