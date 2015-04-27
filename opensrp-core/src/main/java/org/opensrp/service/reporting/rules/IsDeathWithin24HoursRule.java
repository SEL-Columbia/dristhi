package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ChildCloseFormFields.DEATH_CAUSE;
import static org.opensrp.common.AllConstants.ChildCloseFormFields.WITHIN_24HRS_VALUE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsDeathWithin24HoursRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return WITHIN_24HRS_VALUE.equalsIgnoreCase(reportFields.get(DEATH_CAUSE));
    }
}
