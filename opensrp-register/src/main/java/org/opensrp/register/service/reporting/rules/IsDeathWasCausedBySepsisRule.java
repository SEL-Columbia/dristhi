package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildCloseFormFields.DEATH_CAUSE;
import static org.opensrp.common.AllConstants.ChildCloseFormFields.SEPSIS_VALUE;

@Component
public class IsDeathWasCausedBySepsisRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return SEPSIS_VALUE.equalsIgnoreCase(reportFields.get(DEATH_CAUSE));
    }
}
