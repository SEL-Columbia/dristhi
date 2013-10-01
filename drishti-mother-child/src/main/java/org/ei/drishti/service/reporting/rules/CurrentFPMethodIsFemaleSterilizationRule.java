package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.FEMALE_STERILIZATION_FP_METHOD_VALUE;

@Component
public class CurrentFPMethodIsFemaleSterilizationRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return FEMALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(reportFields.get(CURRENT_FP_METHOD_FIELD_NAME));
    }
}

