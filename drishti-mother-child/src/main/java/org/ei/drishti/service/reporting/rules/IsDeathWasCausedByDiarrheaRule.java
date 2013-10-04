package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.DEATH_CAUSE;
import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.DIARRHEA_VALUE;

@Component
public class IsDeathWasCausedByDiarrheaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return DIARRHEA_VALUE.equalsIgnoreCase(reportFields.get(DEATH_CAUSE));
    }
}
