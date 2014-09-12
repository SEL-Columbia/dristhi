package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.EMPTY_STRING;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.HAS_FEVER_FIELD;

@Component
public class IsMotherHasFeverRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return !(EMPTY_STRING.equalsIgnoreCase(reportFields.get(HAS_FEVER_FIELD)));
    }
}
