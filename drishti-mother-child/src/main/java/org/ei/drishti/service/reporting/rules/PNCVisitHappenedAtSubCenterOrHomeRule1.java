package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.CommonFormFields.HOME_FIELD_VALUE;
import static org.ei.drishti.common.AllConstants.CommonFormFields.SUB_CENTER_SERVICE_PROVIDED_PLACE_VALUE;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.VISIT_PLACE_FIELD_NAME;

@Component
public class PNCVisitHappenedAtSubCenterOrHomeRule1 implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return SUB_CENTER_SERVICE_PROVIDED_PLACE_VALUE.equalsIgnoreCase(reportFields.get(VISIT_PLACE_FIELD_NAME))
                || HOME_FIELD_VALUE.equalsIgnoreCase(reportFields.get(VISIT_PLACE_FIELD_NAME));

    }
}
