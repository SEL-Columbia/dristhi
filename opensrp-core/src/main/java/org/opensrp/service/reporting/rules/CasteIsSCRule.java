package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ECRegistrationFields.CASTE;
import static org.opensrp.common.AllConstants.ECRegistrationFields.SC_VALUE;

@Component
public class CasteIsSCRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return SC_VALUE.equalsIgnoreCase(reportFields.get(CASTE));
    }
}
