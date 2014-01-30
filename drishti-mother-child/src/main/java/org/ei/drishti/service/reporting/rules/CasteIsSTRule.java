package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ECRegistrationFields.CASTE;
import static org.ei.drishti.common.AllConstants.ECRegistrationFields.ST_VALUE;

@Component
public class CasteIsSTRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return ST_VALUE.equalsIgnoreCase(reportFields.get(CASTE));
    }
}
