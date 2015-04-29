package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.CommonFormFields.REFERENCE_DATE;

import org.joda.time.LocalDate;
import org.opensrp.common.AllConstants;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class WomanIsDischargedWithin48HoursOfDeliveryRule implements IRule {
    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate deliveryDate = LocalDate.parse(reportFields.get(REFERENCE_DATE));
        LocalDate dischargeDate = LocalDate.parse(reportFields.get(AllConstants.PNCVisitFormFields.DISCHARGE_DATE));

        return dischargeDate.equals(deliveryDate)
                || dischargeDate.equals(deliveryDate.plusDays(1))
                || dischargeDate.equals(deliveryDate.plusDays(2));

    }
}
