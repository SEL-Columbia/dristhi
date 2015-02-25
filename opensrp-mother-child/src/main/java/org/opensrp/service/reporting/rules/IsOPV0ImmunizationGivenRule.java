package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.opensrp.service.reporting.ChildImmunization;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildImmunizationFields.OPV_0_VALUE;

@Component
public class IsOPV0ImmunizationGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return new ChildImmunization().isImmunizedWith(OPV_0_VALUE, reportFields);
    }
}
