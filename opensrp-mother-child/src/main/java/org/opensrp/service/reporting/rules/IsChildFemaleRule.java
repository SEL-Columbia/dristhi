package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.CommonChildFormFields.FEMALE_VALUE;
import static org.opensrp.common.AllConstants.CommonChildFormFields.GENDER;

@Component
public class IsChildFemaleRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return FEMALE_VALUE.equalsIgnoreCase(reportFields.get(GENDER));
    }
}
