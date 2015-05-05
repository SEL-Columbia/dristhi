package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.opensrp.register.service.reporting.ChildImmunization;
import org.opensrp.service.reporting.rules.IRule;
import org.springframework.stereotype.Component;

@Component
public class IsAllImmunizationsGivenEitherWithMeaslesOrMMRRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
       return new ChildImmunization().isImmunizationsGivenWithMeaslesOrMMR(reportFields);
    }
}
