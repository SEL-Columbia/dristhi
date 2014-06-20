package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.IMMEDIATE_REFERRAL;

@Component
public class ShouldMotherBeReferredImmediatelyRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return reportFields.get(IMMEDIATE_REFERRAL).contains(AllConstants.Form.BOOLEAN_TRUE_VALUE);
    }
}
