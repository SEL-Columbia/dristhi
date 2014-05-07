package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.PNCRegistrationFields.IS_SKILLED_DELIVERY;

@Component
public class DeliveryIsAttendedByNonSBATrainedPersonRule implements IRule {
    @Override
    public boolean apply(SafeMap reportFields) {
        return AllConstants.Form.BOOLEAN_FALSE_VALUE.equalsIgnoreCase(reportFields.get(IS_SKILLED_DELIVERY));
    }
}
