package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.CONDOM_FP_METHOD_VALUE;
import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.NEW_FP_METHOD_FIELD_NAME;

@Component
public class NewFPMethodIsCondomRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return CONDOM_FP_METHOD_VALUE.equalsIgnoreCase(reportFields.get(NEW_FP_METHOD_FIELD_NAME));
    }
}

