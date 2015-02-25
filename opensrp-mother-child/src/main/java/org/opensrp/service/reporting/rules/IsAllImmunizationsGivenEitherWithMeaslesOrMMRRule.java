package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.opensrp.service.reporting.ChildImmunization;
import org.springframework.stereotype.Component;

@Component
public class IsAllImmunizationsGivenEitherWithMeaslesOrMMRRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
       return new ChildImmunization().isImmunizationsGivenWithMeaslesOrMMR(reportFields);
    }
}
