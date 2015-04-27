package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.DMPA_INJECTABLE_FP_METHOD_VALUE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class CurrentFPMethodIsDMPAInjectableRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return DMPA_INJECTABLE_FP_METHOD_VALUE.equalsIgnoreCase(reportFields.get(CURRENT_FP_METHOD_FIELD_NAME));
    }
}