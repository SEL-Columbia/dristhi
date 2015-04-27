package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ChildImmunizationFields.MEASLES_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.MMR_VALUE;

import org.opensrp.service.reporting.ChildImmunization;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsAllImmunizationsGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        ChildImmunization childImmunization = new ChildImmunization();
        return childImmunization.isImmunizedWith(MEASLES_VALUE, reportFields) ||
                (childImmunization.isImmunizedWith(MMR_VALUE, reportFields) &&
                        !childImmunization.isPreviouslyImmunizedWith(MEASLES_VALUE, reportFields));
    }
}
