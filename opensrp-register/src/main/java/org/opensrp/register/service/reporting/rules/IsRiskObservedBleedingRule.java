package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ANCVisitFormFields.BLEEDING_VALUE;
import static org.opensrp.common.AllConstants.ANCVisitFormFields.RISK_OBSERVED_DURING_ANC;

@Component
public class IsRiskObservedBleedingRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        String risksObserved = reportFields.get(RISK_OBSERVED_DURING_ANC);
        return (risksObserved != null) && risksObserved.contains(BLEEDING_VALUE);
    }

}
