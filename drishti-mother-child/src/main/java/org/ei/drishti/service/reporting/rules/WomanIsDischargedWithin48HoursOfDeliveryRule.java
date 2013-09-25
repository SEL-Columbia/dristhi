package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class WomanIsDischargedWithin48HoursOfDeliveryRule implements IRule {
    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate deliveryDate = LocalDate.parse(reportFields.get(AllConstants.ANCFormFields.REFERENCE_DATE));
        LocalDate dischargeDate = LocalDate.parse(reportFields.get(AllConstants.PNCVisitFormFields.DISCHARGE_DATE));

        return dischargeDate.equals(deliveryDate)
                || dischargeDate.equals(deliveryDate.plusDays(1))
                || dischargeDate.equals(deliveryDate.plusDays(2));

    }
}
