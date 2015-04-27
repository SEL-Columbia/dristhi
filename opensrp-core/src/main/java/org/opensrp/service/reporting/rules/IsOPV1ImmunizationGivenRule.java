package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ChildImmunizationFields.OPV_1_VALUE;

import org.opensrp.service.reporting.ChildImmunization;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsOPV1ImmunizationGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return new ChildImmunization().isImmunizedWith(OPV_1_VALUE, reportFields);
    }
}
