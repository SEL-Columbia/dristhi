package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ChildCloseFormFields.DEATH_CAUSE;
import static org.opensrp.common.AllConstants.ChildCloseFormFields.OTHERS_LIST;
import static org.opensrp.common.AllConstants.ChildCloseFormFields.OTHERS_VALUE_LIST;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsDeathWasCausedByOthersRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return OTHERS_LIST.contains(reportFields.get(DEATH_CAUSE)) ||
                OTHERS_VALUE_LIST.contains(reportFields.get(DEATH_CAUSE));
    }
}
