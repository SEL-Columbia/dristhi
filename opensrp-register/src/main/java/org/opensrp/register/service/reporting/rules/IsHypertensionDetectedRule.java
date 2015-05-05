package org.opensrp.register.service.reporting.rules;

import org.opensrp.common.util.IntegerUtil;
import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ANCVisitFormFields.BP_DIASTOLIC;
import static org.opensrp.common.AllConstants.ANCVisitFormFields.BP_SYSTOLIC;

@Component
public class IsHypertensionDetectedRule implements IRule {

    private static final int BP_DIASTOLIC_THRESHOLD_VALUE = 90;
    private static final int BP_SYSTOLIC_THRESHOLD_VALUE = 140;

    @Override
    public boolean apply(SafeMap reportFields) {

        return ((IntegerUtil.tryParse(reportFields.get(BP_DIASTOLIC), 0) >= BP_DIASTOLIC_THRESHOLD_VALUE)
                || (IntegerUtil.tryParse(reportFields.get(BP_SYSTOLIC), 0) >= BP_SYSTOLIC_THRESHOLD_VALUE));
    }
}
