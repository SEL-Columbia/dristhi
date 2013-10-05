package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ECCloseFields.MATERNAL_DEATH_CAUSE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ECCloseFields.PROLONGED_OBSTRUCTED_LABOR_VALUE;

@Component
public class IsDeathDueToProlongedObstructedLaborRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return PROLONGED_OBSTRUCTED_LABOR_VALUE.equalsIgnoreCase(reportFields.get(MATERNAL_DEATH_CAUSE_FIELD_NAME));
    }
}

