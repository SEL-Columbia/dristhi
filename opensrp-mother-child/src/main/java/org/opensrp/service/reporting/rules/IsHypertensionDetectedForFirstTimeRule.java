package org.opensrp.service.reporting.rules;

import org.opensrp.common.AllConstants;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ANCVisitFormFields.IS_HYPERTENSION_DETECTED_FOR_FIRST_TIME;

@Component
public class IsHypertensionDetectedForFirstTimeRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return AllConstants.BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportFields.get(IS_HYPERTENSION_DETECTED_FOR_FIRST_TIME));
    }
}
