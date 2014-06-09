package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.service.reporting.ChildImmunization;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.JE_VALUE;

@Component
public class IsJEImmunizationGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return new ChildImmunization().isImmunizedWith(JE_VALUE, reportFields);
    }
}
