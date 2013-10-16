package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.util.IntegerUtil;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCVisitFormFields.BP_DIASTOLIC;
import static org.ei.drishti.common.AllConstants.ANCVisitFormFields.BP_SYSTOLIC;

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
