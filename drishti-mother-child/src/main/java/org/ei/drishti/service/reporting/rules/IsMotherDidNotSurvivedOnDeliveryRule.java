package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.DID_MOTHER_SURVIVE;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.DID_WOMAN_SURVIVE;
import static org.ei.drishti.common.AllConstants.Form.BOOLEAN_FALSE_VALUE;

@Component
public class IsMotherDidNotSurvivedOnDeliveryRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return reportFields.get(DID_WOMAN_SURVIVE).equalsIgnoreCase(BOOLEAN_FALSE_VALUE)
                || reportFields.get(DID_MOTHER_SURVIVE).equalsIgnoreCase(BOOLEAN_FALSE_VALUE);
    }
}
