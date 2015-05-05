package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.opensrp.register.service.reporting.ChildImmunization;
import org.opensrp.service.reporting.rules.IRule;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ChildImmunizationFields.PENTAVALENT_2_VALUE;

@Component
public class IsPentavalent2ImmunizationGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return new ChildImmunization().isImmunizedWith(PENTAVALENT_2_VALUE, reportFields);
    }
}
