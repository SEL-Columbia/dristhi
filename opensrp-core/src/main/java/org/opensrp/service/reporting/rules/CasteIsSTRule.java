package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ECRegistrationFields.CASTE;
import static org.opensrp.common.AllConstants.ECRegistrationFields.ST_VALUE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class CasteIsSTRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return ST_VALUE.equalsIgnoreCase(reportFields.get(CASTE));
    }
}
