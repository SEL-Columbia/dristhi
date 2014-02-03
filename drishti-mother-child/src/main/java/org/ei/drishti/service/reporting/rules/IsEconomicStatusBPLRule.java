package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ECRegistrationFields.ECONOMIC_STATUS;
import static org.ei.drishti.common.AllConstants.ECRegistrationFields.ECONOMIC_STATUS_BPL_VALUE;

@Component
public class IsEconomicStatusBPLRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return ECONOMIC_STATUS_BPL_VALUE.equalsIgnoreCase(reportFields.get(ECONOMIC_STATUS));
    }
}

