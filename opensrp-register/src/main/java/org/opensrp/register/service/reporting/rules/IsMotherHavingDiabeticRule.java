package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ANCInvestigationsFormFields.TESTS_POSITIVE_RESULTS;
import static org.opensrp.common.AllConstants.ANCInvestigationsFormFields.URINE_SUGAR_VALUE;

@Component
public class IsMotherHavingDiabeticRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        String testsWithPositiveResult = reportFields.get(TESTS_POSITIVE_RESULTS);
        return testsWithPositiveResult != null && testsWithPositiveResult.contains(URINE_SUGAR_VALUE);
    }
}
