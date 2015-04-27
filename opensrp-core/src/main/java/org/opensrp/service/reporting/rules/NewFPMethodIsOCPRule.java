package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.NEW_FP_METHOD_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.OCP_FP_METHOD_VALUE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class NewFPMethodIsOCPRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return OCP_FP_METHOD_VALUE.equalsIgnoreCase(reportFields.get(NEW_FP_METHOD_FIELD_NAME));
    }
}

