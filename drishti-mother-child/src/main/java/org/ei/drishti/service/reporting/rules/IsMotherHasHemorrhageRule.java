package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.HEAVY_BLEEDING_VALUE;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.VAGINAL_PROBLEMS_FIELD_NAME;

@Component
public class IsMotherHasHemorrhageRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return reportFields.get(VAGINAL_PROBLEMS_FIELD_NAME).contains(HEAVY_BLEEDING_VALUE);
    }
}
