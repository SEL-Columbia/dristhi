package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ECRegistrationFields.IS_OUT_OF_AREA;
import static org.opensrp.common.AllConstants.ECRegistrationFields.IS_OUT_OF_AREA_FALSE;

@Component
public class IsNotOutOfAreaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return IS_OUT_OF_AREA_FALSE.equalsIgnoreCase(reportFields.get(IS_OUT_OF_AREA));
    }
}