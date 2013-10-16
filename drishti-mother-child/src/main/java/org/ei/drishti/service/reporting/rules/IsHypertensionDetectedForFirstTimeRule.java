package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCVisitFormFields.IS_HYPERTENSION_DETECTED_FOR_FIRST_TIME;

@Component
public class IsHypertensionDetectedForFirstTimeRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return AllConstants.BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportFields.get(IS_HYPERTENSION_DETECTED_FOR_FIRST_TIME));
    }
}
