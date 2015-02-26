package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.CommonFormFields.SERVICE_PROVIDED_DATE;
import static org.opensrp.common.AllConstants.ChildRegistrationFormFields.DATE_OF_BIRTH;

@Component
public class IsChildMoreThan16MonthsRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate dateOfBirth = LocalDate.parse(reportFields.get(DATE_OF_BIRTH));
        LocalDate reportDate = LocalDate.parse(reportFields.get(SERVICE_PROVIDED_DATE));
        return dateOfBirth.plusMonths(16).isBefore(reportDate);
    }
}
