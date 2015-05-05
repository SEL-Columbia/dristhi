package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.CommonFormFields.*;

@Component
public class SpontaneousAbortionAtHomeAtHomeOrSubCenterRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return SUB_CENTER_SERVICE_PROVIDED_PLACE_VALUE.equalsIgnoreCase(reportFields.get(SERVICE_PROVIDED_PLACE)) ||
                HOME_FIELD_VALUE.equalsIgnoreCase(reportFields.get(SERVICE_PROVIDED_PLACE));
    }
}

