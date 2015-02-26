package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.opensrp.service.reporting.ChildImmunization;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildImmunizationFields.OPV_BOOSTER_VALUE;

@Component
public class IsOPVBoosterImmunizationGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return  new ChildImmunization().isImmunizedWith(OPV_BOOSTER_VALUE, reportFields);
    }
}
