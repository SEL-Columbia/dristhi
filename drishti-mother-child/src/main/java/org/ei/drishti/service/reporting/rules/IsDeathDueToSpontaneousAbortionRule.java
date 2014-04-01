package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCCloseFields.SPONTANEOUS_ABORTION_VALUE;
import static org.ei.drishti.common.AllConstants.EntityCloseFormFields.CLOSE_REASON_FIELD_NAME;

@Component
public class IsDeathDueToSpontaneousAbortionRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return SPONTANEOUS_ABORTION_VALUE.equalsIgnoreCase(reportFields.get(CLOSE_REASON_FIELD_NAME));
    }
}

