package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCRegistrationFormFields.JSY_BENEFICIARY;
import static org.ei.drishti.common.AllConstants.ANCRegistrationFormFields.JSY_BENEFICIARY_TRUE_VALUE;

@Component
public class JsyBeneficiaryIsTrueRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return JSY_BENEFICIARY_TRUE_VALUE.equalsIgnoreCase(reportFields.get(JSY_BENEFICIARY));
    }
}
