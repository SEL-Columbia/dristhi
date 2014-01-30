package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.CURRENT_FP_METHOD_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.IUD_FP_METHOD_VALUE;

@Component
public class CurrentFPMethodIsIUDRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return IUD_FP_METHOD_VALUE.equalsIgnoreCase(reportFields.get(CURRENT_FP_METHOD_FIELD_NAME));
    }
}