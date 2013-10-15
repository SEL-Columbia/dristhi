package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.service.reporting.ChildImmunization;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.DPT_BOOSTER_2_VALUE;

@Component
public class IsDPTBooster2ImmunizationGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return new ChildImmunization().isImmunizedWith(DPT_BOOSTER_2_VALUE, reportFields);
    }
}
