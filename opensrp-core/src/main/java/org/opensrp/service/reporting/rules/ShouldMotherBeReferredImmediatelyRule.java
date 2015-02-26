package org.opensrp.service.reporting.rules;

import org.opensrp.common.AllConstants;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.PNCVisitFormFields.IMMEDIATE_REFERRAL;

@Component
public class ShouldMotherBeReferredImmediatelyRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return reportFields.get(IMMEDIATE_REFERRAL).contains(AllConstants.Form.BOOLEAN_TRUE_VALUE);
    }
}
