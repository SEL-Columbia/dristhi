package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildCloseFormFields.DEATH_CAUSE;
import static org.opensrp.common.AllConstants.ChildCloseFormFields.DIARRHEA_VALUE;

@Component
public class IsDeathWasCausedByDiarrheaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return DIARRHEA_VALUE.equalsIgnoreCase(reportFields.get(DEATH_CAUSE));
    }
}
