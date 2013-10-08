package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.service.reporting.ChildImmunization;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsAllImmunizationsGivenEitherWithMeaslesOrMMRRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
       return new ChildImmunization().isImmunizationsGivenWithMeaslesOrMMR(reportFields);
    }
}
