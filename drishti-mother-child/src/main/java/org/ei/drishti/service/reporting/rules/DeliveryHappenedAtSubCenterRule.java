package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.DELIVERY_PLACE;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.SC_FIELD_VALUE;

@Component
public class DeliveryHappenedAtSubCenterRule implements IRule {
    @Override
    public boolean apply(SafeMap reportFields) {
        return SC_FIELD_VALUE.equalsIgnoreCase(reportFields.get(DELIVERY_PLACE));
    }
}
