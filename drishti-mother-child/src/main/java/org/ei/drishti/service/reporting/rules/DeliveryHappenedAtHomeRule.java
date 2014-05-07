package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.PNCRegistrationFields.DELIVERY_PLACE;
import static org.ei.drishti.common.AllConstants.CommonFormFields.HOME_FIELD_VALUE;

@Component
public class DeliveryHappenedAtHomeRule implements IRule {
    @Override
    public boolean apply(SafeMap reportFields) {
        return HOME_FIELD_VALUE.equalsIgnoreCase(reportFields.get(DELIVERY_PLACE));
    }
}
