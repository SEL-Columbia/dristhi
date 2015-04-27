package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ChildImmunizationFields.OPV_3_VALUE;

import org.opensrp.service.reporting.ChildImmunization;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsOPV3ImmunizationGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return new ChildImmunization().isImmunizedWith(OPV_3_VALUE, reportFields);
    }
}
