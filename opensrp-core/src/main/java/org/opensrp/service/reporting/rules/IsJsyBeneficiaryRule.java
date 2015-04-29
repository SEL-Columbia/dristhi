package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ANCRegistrationFormFields.JSY_BENEFICIARY;

import org.opensrp.common.AllConstants;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsJsyBeneficiaryRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return AllConstants.Form.BOOLEAN_TRUE_VALUE.equalsIgnoreCase(reportFields.get(JSY_BENEFICIARY));
    }
}
