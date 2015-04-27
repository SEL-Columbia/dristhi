package org.opensrp.service.reporting.rules;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.opensrp.common.AllConstants.ChildRegistrationFormFields.DATE_OF_BIRTH;
import static org.opensrp.common.AllConstants.CommonFormFields.SERVICE_PROVIDED_DATE;

import org.joda.time.LocalDate;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsChildLessThanOneYearOldRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate dateOfBirth = LocalDate.parse(reportFields.get(DATE_OF_BIRTH));
        if (isBlank(reportFields.get(SERVICE_PROVIDED_DATE)))
            return false;
        LocalDate serviceProvidedDate = LocalDate.parse(reportFields.get(SERVICE_PROVIDED_DATE));
        return dateOfBirth.plusYears(1).isAfter(serviceProvidedDate);
    }
}
