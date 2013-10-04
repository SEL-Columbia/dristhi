package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.DEATH_CAUSE;
import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.MEASLES_VALUE;

@Component
public class IsDeathWasCausedByMeaslesRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return MEASLES_VALUE.equalsIgnoreCase(reportFields.get(DEATH_CAUSE));
    }
}
