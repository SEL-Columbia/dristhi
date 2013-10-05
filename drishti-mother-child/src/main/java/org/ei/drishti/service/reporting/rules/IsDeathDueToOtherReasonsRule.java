package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ChildCloseFormFields.OTHERS_LIST;
import static org.ei.drishti.common.AllConstants.ECCloseFields.MATERNAL_DEATH_CAUSE_FIELD_NAME;

@Component
public class IsDeathDueToOtherReasonsRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return OTHERS_LIST.contains(reportFields.get(MATERNAL_DEATH_CAUSE_FIELD_NAME));
    }
}

