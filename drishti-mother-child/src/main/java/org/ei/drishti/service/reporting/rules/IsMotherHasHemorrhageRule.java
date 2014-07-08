package org.ei.drishti.service.reporting.rules;

import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.HEAVY_BLEEDING_VALUE;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.VAGINAL_PROBLEMS_FIELD_NAME;

@Component
public class IsMotherHasHemorrhageRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return StringUtils.contains(reportFields.get(VAGINAL_PROBLEMS_FIELD_NAME), HEAVY_BLEEDING_VALUE);
    }
}
