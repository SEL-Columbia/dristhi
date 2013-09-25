package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCRegistrationFormFields.JSY_BENEFICIARY;

@Component
public class IsJsyBeneficiaryRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return AllConstants.Form.BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportFields.get(JSY_BENEFICIARY));
    }
}
