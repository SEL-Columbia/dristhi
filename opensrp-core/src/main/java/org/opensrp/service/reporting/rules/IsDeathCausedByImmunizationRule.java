package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ChildCloseFormFields.IS_IMMUNIZATION_DEATH;
import static org.opensrp.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsDeathCausedByImmunizationRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportFields.get(IS_IMMUNIZATION_DEATH));
    }
}

