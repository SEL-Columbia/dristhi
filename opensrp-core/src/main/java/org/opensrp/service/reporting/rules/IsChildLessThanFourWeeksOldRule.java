package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildRegistrationFormFields.DATE_OF_BIRTH;
import static org.opensrp.common.AllConstants.CommonFormFields.SERVICE_PROVIDED_DATE;

@Component
public class IsChildLessThanFourWeeksOldRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate dateOfBirth = LocalDate.parse(reportFields.get(DATE_OF_BIRTH));
        String serviceProvidedDate = reportFields.get(SERVICE_PROVIDED_DATE);

        return serviceProvidedDate != null ? dateOfBirth.plusWeeks(4).isAfter(LocalDate.parse(serviceProvidedDate)) : false;
    }
}
