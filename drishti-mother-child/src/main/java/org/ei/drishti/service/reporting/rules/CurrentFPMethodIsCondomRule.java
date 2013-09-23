package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.CONDOM_FP_METHOD_VALUE;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;

@Component
public class CurrentFPMethodIsCondomRule implements IRule {

    @Override
    public boolean apply(SafeMap safeMap) {
        return CONDOM_FP_METHOD_VALUE.equalsIgnoreCase(safeMap.get(CURRENT_FP_METHOD_FIELD_NAME));
    }
}

