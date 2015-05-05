package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ECRegistrationFields.ECONOMIC_STATUS;
import static org.opensrp.common.AllConstants.ECRegistrationFields.ECONOMIC_STATUS_BPL_VALUE;

@Component
public class IsEconomicStatusBPLRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return ECONOMIC_STATUS_BPL_VALUE.equalsIgnoreCase(reportFields.get(ECONOMIC_STATUS));
    }
}

