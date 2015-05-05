package org.opensrp.register.service.reporting.rules;

import org.opensrp.common.AllConstants;
import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.DeliveryOutcomeFields.IS_SKILLED_DELIVERY;

@Component
public class DeliveryIsAttendedByNonSBATrainedPersonRule implements IRule {
    @Override
    public boolean apply(SafeMap reportFields) {
        return AllConstants.Form.BOOLEAN_FALSE_VALUE.equalsIgnoreCase(reportFields.get(IS_SKILLED_DELIVERY));
    }
}
