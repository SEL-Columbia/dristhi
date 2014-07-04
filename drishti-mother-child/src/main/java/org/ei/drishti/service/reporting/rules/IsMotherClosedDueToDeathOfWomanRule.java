package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCCloseFields.DEATH_OF_WOMAN_VALUE;
import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.CLOSE_REASON_FIELD_NAME;

@Component
public class IsMotherClosedDueToDeathOfWomanRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return DEATH_OF_WOMAN_VALUE.equalsIgnoreCase(reportFields.get(CLOSE_REASON_FIELD_NAME));
    }
}
