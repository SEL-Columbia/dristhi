package org.opensrp.register.service.reporting.rules;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.STERILIZATION_SIDE_EFFECT_FIELD_NAME;

@Component
public class IsSideEffectDueToSterilizationRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return !StringUtils.isBlank(reportFields.get(STERILIZATION_SIDE_EFFECT_FIELD_NAME));
    }
}

