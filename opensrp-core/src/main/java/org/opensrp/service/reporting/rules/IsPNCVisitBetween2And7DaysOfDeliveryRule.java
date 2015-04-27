package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.CommonFormFields.REFERENCE_DATE;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VISIT_DATE_FIELD_NAME;

import org.joda.time.LocalDate;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsPNCVisitBetween2And7DaysOfDeliveryRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate pncVisitDate = LocalDate.parse(reportFields.get(VISIT_DATE_FIELD_NAME));
        LocalDate dateOfBirth = LocalDate.parse(reportFields.get(REFERENCE_DATE));

        LocalDate twoDaysAfterDateOfBirth = dateOfBirth.plusDays(2);
        LocalDate sevenDaysAfterDateOfBirth = dateOfBirth.plusDays(7);

        return pncVisitDate.isAfter(twoDaysAfterDateOfBirth) && pncVisitDate.isBefore(sevenDaysAfterDateOfBirth);
    }
}
