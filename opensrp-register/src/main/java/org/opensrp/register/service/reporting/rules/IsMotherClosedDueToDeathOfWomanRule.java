package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ANCCloseFields.DEATH_OF_WOMAN_VALUE;
import static org.opensrp.common.AllConstants.ChildCloseFormFields.CLOSE_REASON_FIELD_NAME;

@Component
public class IsMotherClosedDueToDeathOfWomanRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return DEATH_OF_WOMAN_VALUE.equalsIgnoreCase(reportFields.get(CLOSE_REASON_FIELD_NAME));
    }
}
