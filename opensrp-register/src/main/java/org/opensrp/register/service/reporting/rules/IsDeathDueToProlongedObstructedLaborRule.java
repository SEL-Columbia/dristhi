package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ECCloseFields.MATERNAL_DEATH_CAUSE_FIELD_NAME;
import static org.opensrp.common.AllConstants.ECCloseFields.PROLONGED_OBSTRUCTED_LABOR_VALUE;

@Component
public class IsDeathDueToProlongedObstructedLaborRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return PROLONGED_OBSTRUCTED_LABOR_VALUE.equalsIgnoreCase(reportFields.get(MATERNAL_DEATH_CAUSE_FIELD_NAME));
    }
}

