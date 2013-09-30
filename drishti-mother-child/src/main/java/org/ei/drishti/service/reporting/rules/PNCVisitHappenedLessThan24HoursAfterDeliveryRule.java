package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.CommonFormFields.REFERENCE_DATE;

@Component
public class PNCVisitHappenedLessThan24HoursAfterDeliveryRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate deliveryDate = LocalDate.parse(reportFields.get(REFERENCE_DATE));
        LocalDate pncVisitDate = LocalDate.parse(reportFields.get(AllConstants.PNCVisitFormFields.VISIT_DATE_FIELD_NAME));

        return pncVisitDate.equals(deliveryDate) || pncVisitDate.equals(nextDay(deliveryDate));
    }

    private LocalDate nextDay(LocalDate deliveryDate) {
        return deliveryDate.plusDays(1);
    }
}
