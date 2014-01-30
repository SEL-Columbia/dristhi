package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ECRegistrationFields.CASTE;
import static org.ei.drishti.common.AllConstants.ECRegistrationFields.C_OTHERS_VALUE;

@Component
public class CasteIsOthersRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return C_OTHERS_VALUE.equalsIgnoreCase(reportFields.get(CASTE));
    }
}
