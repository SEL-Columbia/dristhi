package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.IS_SKILLED_DELIVERY;

@Component
public class DeliveryIsAttendedBySBATrainedPersonRule implements IRule {
    @Override
    public boolean apply(SafeMap reportFields) {
        return AllConstants.Form.BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportFields.get(IS_SKILLED_DELIVERY));
    }
}
