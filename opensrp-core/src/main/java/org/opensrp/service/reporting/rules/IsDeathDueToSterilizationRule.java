package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ECCloseFields.IS_STERILIZATION_DEATH_FIELD_NAME;
import static org.opensrp.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;

@Component
public class IsDeathDueToSterilizationRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportFields.get(IS_STERILIZATION_DEATH_FIELD_NAME));
    }
}

