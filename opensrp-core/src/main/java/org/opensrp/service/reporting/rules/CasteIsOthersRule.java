package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ECRegistrationFields.CASTE;
import static org.opensrp.common.AllConstants.ECRegistrationFields.C_OTHERS_VALUE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class CasteIsOthersRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return C_OTHERS_VALUE.equalsIgnoreCase(reportFields.get(CASTE));
    }
}
