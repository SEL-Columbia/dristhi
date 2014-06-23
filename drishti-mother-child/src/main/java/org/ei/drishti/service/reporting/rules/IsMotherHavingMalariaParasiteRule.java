package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCInvestigationsFormFields.MALARIA_VALUE;
import static org.ei.drishti.common.AllConstants.ANCInvestigationsFormFields.TESTS_POSITIVE_RESULTS;

@Component
public class IsMotherHavingMalariaParasiteRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return reportFields.get(TESTS_POSITIVE_RESULTS).contains(MALARIA_VALUE);
    }
}
