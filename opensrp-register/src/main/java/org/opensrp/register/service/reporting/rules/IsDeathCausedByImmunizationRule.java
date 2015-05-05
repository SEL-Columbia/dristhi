package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildCloseFormFields.IS_IMMUNIZATION_DEATH;
import static org.opensrp.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;

@Component
public class IsDeathCausedByImmunizationRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportFields.get(IS_IMMUNIZATION_DEATH));
    }
}

