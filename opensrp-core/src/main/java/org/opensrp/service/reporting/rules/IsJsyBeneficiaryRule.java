package org.opensrp.service.reporting.rules;

import org.opensrp.common.AllConstants;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ANCRegistrationFormFields.JSY_BENEFICIARY;

@Component
public class IsJsyBeneficiaryRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return AllConstants.Form.BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportFields.get(JSY_BENEFICIARY));
    }
}
