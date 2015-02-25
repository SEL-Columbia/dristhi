package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildCloseFormFields.CLOSE_REASON_FIELD_NAME;
import static org.opensrp.common.AllConstants.ChildCloseFormFields.DEATH_OF_CHILD_VALUE;

@Component
public class IsChildClosedDueToDeathRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return DEATH_OF_CHILD_VALUE.equalsIgnoreCase(reportFields.get(CLOSE_REASON_FIELD_NAME));
    }
}
