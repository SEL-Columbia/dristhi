package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ECRegistrationFields.ECONOMIC_STATUS;
import static org.opensrp.common.AllConstants.ECRegistrationFields.ECONOMIC_STATUS_APL_VALUE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsEconomicStatusAPLRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return ECONOMIC_STATUS_APL_VALUE.equalsIgnoreCase(reportFields.get(ECONOMIC_STATUS));
    }
}

