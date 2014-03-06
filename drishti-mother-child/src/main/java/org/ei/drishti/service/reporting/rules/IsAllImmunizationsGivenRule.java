package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.service.reporting.ChildImmunization;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.MEASLES_VALUE;
import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.MMR_VALUE;

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
