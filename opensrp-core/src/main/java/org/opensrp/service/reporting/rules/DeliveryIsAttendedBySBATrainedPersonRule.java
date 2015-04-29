package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.DeliveryOutcomeFields.IS_SKILLED_DELIVERY;

import org.opensrp.common.AllConstants;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class DeliveryIsAttendedBySBATrainedPersonRule implements IRule {
    @Override
    public boolean apply(SafeMap reportFields) {
        return AllConstants.Form.BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportFields.get(IS_SKILLED_DELIVERY));
    }
}
