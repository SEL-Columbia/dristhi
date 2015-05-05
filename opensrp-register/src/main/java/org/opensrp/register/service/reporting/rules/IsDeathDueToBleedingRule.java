package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ECCloseFields.BLEEDING_VALUE;
import static org.opensrp.common.AllConstants.ECCloseFields.MATERNAL_DEATH_CAUSE_FIELD_NAME;

@Component
public class IsDeathDueToBleedingRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return BLEEDING_VALUE.equalsIgnoreCase(reportFields.get(MATERNAL_DEATH_CAUSE_FIELD_NAME));
    }
}

