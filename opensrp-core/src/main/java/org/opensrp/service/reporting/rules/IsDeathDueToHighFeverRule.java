package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ECCloseFields.HIGH_FEVER_VALUE;
import static org.opensrp.common.AllConstants.ECCloseFields.MATERNAL_DEATH_CAUSE_FIELD_NAME;

@Component
public class IsDeathDueToHighFeverRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return HIGH_FEVER_VALUE.equalsIgnoreCase(reportFields.get(MATERNAL_DEATH_CAUSE_FIELD_NAME));
    }
}

