package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ECRegistrationFields.CASTE;
import static org.ei.drishti.common.AllConstants.ECRegistrationFields.SC_VALUE;

@Component
public class CasteIsSCRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return SC_VALUE.equalsIgnoreCase(reportFields.get(CASTE));
    }
}
