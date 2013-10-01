package org.ei.drishti.service.reporting.rules;

import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.STERILIZATION_SIDE_EFFECT_FIELD_NAME;

@Component
public class IsSideEffectDueToSterilizationRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return !StringUtils.isBlank(reportFields.get(STERILIZATION_SIDE_EFFECT_FIELD_NAME));
    }
}

