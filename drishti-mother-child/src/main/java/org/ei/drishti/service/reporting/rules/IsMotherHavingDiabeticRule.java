package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCInvestigationsFormFields.TESTS_POSITIVE_RESULTS;
import static org.ei.drishti.common.AllConstants.ANCInvestigationsFormFields.URINE_SUGAR_VALUE;

@Component
public class IsMotherHavingDiabeticRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        String testsWithPositiveResult = reportFields.get(TESTS_POSITIVE_RESULTS);
        return testsWithPositiveResult != null && testsWithPositiveResult.contains(URINE_SUGAR_VALUE);
    }
}
