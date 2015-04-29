package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ECCloseFields.BLEEDING_VALUE;
import static org.opensrp.common.AllConstants.ECCloseFields.MATERNAL_DEATH_CAUSE_FIELD_NAME;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsDeathDueToBleedingRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return BLEEDING_VALUE.equalsIgnoreCase(reportFields.get(MATERNAL_DEATH_CAUSE_FIELD_NAME));
    }
}

