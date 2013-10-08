package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.CommonChildFormFields.FEMALE_VALUE;
import static org.ei.drishti.common.AllConstants.CommonChildFormFields.GENDER;

@Component
public class IsChildFemaleRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return FEMALE_VALUE.equalsIgnoreCase(reportFields.get(GENDER));
    }
}
