package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildCloseFormFields.DEATH_CAUSE;
import static org.opensrp.common.AllConstants.ChildCloseFormFields.LBW_VALUE;

@Component
public class IsDeathWasCausedByLBWRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return LBW_VALUE.equalsIgnoreCase(reportFields.get(DEATH_CAUSE));
    }
}
