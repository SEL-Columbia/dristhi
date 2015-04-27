package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.CENTCHROMAN_FP_METHOD_VALUE;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class CurrentFPMethodIsCentchromanRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return CENTCHROMAN_FP_METHOD_VALUE.equalsIgnoreCase(reportFields.get(CURRENT_FP_METHOD_FIELD_NAME));
    }
}