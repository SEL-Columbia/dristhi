package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ChildRegistrationFormFields.DATE_OF_BIRTH;
import static org.opensrp.common.AllConstants.CommonFormFields.SERVICE_PROVIDED_DATE;

import org.joda.time.LocalDate;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsChildMoreThan5YearsOldRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate dateOfBirth = LocalDate.parse(reportFields.get(DATE_OF_BIRTH));
        LocalDate serviceProvidedDate = LocalDate.parse(reportFields.get(SERVICE_PROVIDED_DATE));

        return dateOfBirth.plusYears(5).isBefore(serviceProvidedDate);
    }
}
