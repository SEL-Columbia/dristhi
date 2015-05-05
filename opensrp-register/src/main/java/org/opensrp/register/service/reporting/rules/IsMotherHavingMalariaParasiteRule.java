package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ANCInvestigationsFormFields.MALARIA_VALUE;
import static org.opensrp.common.AllConstants.ANCInvestigationsFormFields.TESTS_POSITIVE_RESULTS;

@Component
public class IsMotherHavingMalariaParasiteRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        String testsWithPositiveResult = reportFields.get(TESTS_POSITIVE_RESULTS);
        return testsWithPositiveResult != null ? testsWithPositiveResult.contains(MALARIA_VALUE) : false;
    }
}
