package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.CommonChildFormFields.GENDER;
import static org.ei.drishti.common.AllConstants.CommonChildFormFields.MALE_VALUE;

@Component
public class IsChildMaleRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return MALE_VALUE.equalsIgnoreCase(reportFields.get(GENDER));
    }
}
