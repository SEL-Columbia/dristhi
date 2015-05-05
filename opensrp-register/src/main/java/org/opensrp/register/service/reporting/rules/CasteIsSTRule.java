package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ECRegistrationFields.CASTE;
import static org.opensrp.common.AllConstants.ECRegistrationFields.ST_VALUE;

@Component
public class CasteIsSTRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return ST_VALUE.equalsIgnoreCase(reportFields.get(CASTE));
    }
}
