package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ECRegistrationFields.IS_OUT_OF_AREA;
import static org.ei.drishti.common.AllConstants.ECRegistrationFields.IS_OUT_OF_AREA_FALSE;

@Component
public class IsNotOutOfAreaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return IS_OUT_OF_AREA_FALSE.equalsIgnoreCase(reportFields.get(IS_OUT_OF_AREA));
    }
}