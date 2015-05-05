package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.CommonFormFields.REFERENCE_DATE;
import static org.opensrp.common.AllConstants.CommonFormFields.SERVICE_PROVIDED_DATE;

@Component
public class IsDeliveryDateOneYearInThePastRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate dateOfBirth = LocalDate.parse(reportFields.get(REFERENCE_DATE));
        LocalDate serviceProvidedDate = LocalDate.parse(reportFields.get(SERVICE_PROVIDED_DATE));

        return dateOfBirth.plusYears(1).isAfter(serviceProvidedDate);
    }
}
