package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.Form.BOOLEAN_TRUE_VALUE;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.HAS_FEVER_FIELD;

@Component
public class IsMotherDoesNotHaveFeverRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return !BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportFields.get(HAS_FEVER_FIELD));
    }
}
