package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.DEATH_CAUSE;
import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.FEVER_RELATED_VALUE;

@Component
public class IsDeathWasCausedByFeverRelatedRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return FEVER_RELATED_VALUE.equalsIgnoreCase(reportFields.get(DEATH_CAUSE));
    }
}
