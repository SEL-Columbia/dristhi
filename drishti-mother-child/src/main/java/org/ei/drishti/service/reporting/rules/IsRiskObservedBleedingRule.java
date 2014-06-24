package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCVisitFormFields.BLEEDING_VALUE;
import static org.ei.drishti.common.AllConstants.ANCVisitFormFields.RISK_OBSERVED_DURING_ANC;

@Component
public class IsRiskObservedBleedingRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return reportFields.get(RISK_OBSERVED_DURING_ANC).contains(BLEEDING_VALUE);
    }

}
