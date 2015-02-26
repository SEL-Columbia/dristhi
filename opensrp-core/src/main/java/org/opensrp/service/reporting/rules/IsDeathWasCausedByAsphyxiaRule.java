package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildCloseFormFields.ASPHYXIA_VALUE;
import static org.opensrp.common.AllConstants.ChildCloseFormFields.DEATH_CAUSE;

@Component
public class IsDeathWasCausedByAsphyxiaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return ASPHYXIA_VALUE.equalsIgnoreCase(reportFields.get(DEATH_CAUSE));
    }
}
