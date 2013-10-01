package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.*;

@Component
public class CurrentFPMethodIsEitherMaleOrFemaleSterilizationRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return FEMALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(reportFields.get(CURRENT_FP_METHOD_FIELD_NAME)) ||
                MALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(reportFields.get(CURRENT_FP_METHOD_FIELD_NAME));
    }
}

