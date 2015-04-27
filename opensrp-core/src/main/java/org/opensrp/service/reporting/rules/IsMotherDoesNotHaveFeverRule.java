package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.EMPTY_STRING;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.HAS_FEVER_FIELD;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsMotherDoesNotHaveFeverRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return EMPTY_STRING.equalsIgnoreCase(reportFields.get(HAS_FEVER_FIELD));
    }
}
