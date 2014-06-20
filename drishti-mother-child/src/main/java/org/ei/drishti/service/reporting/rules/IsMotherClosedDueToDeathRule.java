package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.CLOSE_REASON_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.PNCCloseFields.DEATH_OF_MOTHER_VALUE;

@Component
public class IsMotherClosedDueToDeathRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return DEATH_OF_MOTHER_VALUE.equalsIgnoreCase(reportFields.get(CLOSE_REASON_FIELD_NAME));
    }
}
