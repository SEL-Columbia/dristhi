package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.opensrp.service.reporting.ChildImmunization;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildImmunizationFields.MMR_VALUE;

@Component
public class IsMMRImmunizationGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return  new ChildImmunization().isImmunizedWith(MMR_VALUE, reportFields);
    }
}
