package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ChildImmunizationFields.PENTAVALENT_1_VALUE;

import org.opensrp.service.reporting.ChildImmunization;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsPentavalent1ImmunizationGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return new ChildImmunization().isImmunizedWith(PENTAVALENT_1_VALUE, reportFields);
    }

}
