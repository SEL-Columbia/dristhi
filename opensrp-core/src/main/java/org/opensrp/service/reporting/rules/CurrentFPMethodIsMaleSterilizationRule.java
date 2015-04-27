package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.MALE_STERILIZATION_FP_METHOD_VALUE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class CurrentFPMethodIsMaleSterilizationRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return MALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(reportFields.get(CURRENT_FP_METHOD_FIELD_NAME));
    }
}

