package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.DeliveryOutcomeFields.DID_MOTHER_SURVIVE;
import static org.opensrp.common.AllConstants.DeliveryOutcomeFields.DID_WOMAN_SURVIVE;
import static org.opensrp.common.AllConstants.Form.BOOLEAN_FALSE_VALUE;

@Component
public class IsMotherDidNotSurvivedOnDeliveryRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        String didWomanSurvive = reportFields.get(DID_WOMAN_SURVIVE);
        String didMotherSurvive = reportFields.get(DID_MOTHER_SURVIVE);
        return (didWomanSurvive != null && didWomanSurvive.equalsIgnoreCase(BOOLEAN_FALSE_VALUE))
                || (didMotherSurvive != null && didMotherSurvive.equalsIgnoreCase(BOOLEAN_FALSE_VALUE));
    }
}
