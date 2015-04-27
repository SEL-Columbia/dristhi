package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ChildCloseFormFields.DEATH_CAUSE;
import static org.opensrp.common.AllConstants.ChildCloseFormFields.PNEUMONIA_VALUE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsDeathWasCausedByPneumoniaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return PNEUMONIA_VALUE.equalsIgnoreCase(reportFields.get(DEATH_CAUSE));
    }
}
