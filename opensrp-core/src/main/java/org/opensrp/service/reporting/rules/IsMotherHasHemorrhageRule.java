package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.PNCVisitFormFields.HEAVY_BLEEDING_VALUE;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VAGINAL_PROBLEMS_FIELD_NAME;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsMotherHasHemorrhageRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return StringUtils.contains(reportFields.get(VAGINAL_PROBLEMS_FIELD_NAME), HEAVY_BLEEDING_VALUE);
    }
}
