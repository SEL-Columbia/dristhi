package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ECRegistrationFields.IS_OUT_OF_AREA;
import static org.opensrp.common.AllConstants.ECRegistrationFields.IS_OUT_OF_AREA_FALSE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsNotOutOfAreaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return IS_OUT_OF_AREA_FALSE.equalsIgnoreCase(reportFields.get(IS_OUT_OF_AREA));
    }
}