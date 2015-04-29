package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ANCCloseFields.SPONTANEOUS_ABORTION_VALUE;
import static org.opensrp.common.AllConstants.EntityCloseFormFields.CLOSE_REASON_FIELD_NAME;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsDeathDueToSpontaneousAbortionRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return SPONTANEOUS_ABORTION_VALUE.equalsIgnoreCase(reportFields.get(CLOSE_REASON_FIELD_NAME));
    }
}

