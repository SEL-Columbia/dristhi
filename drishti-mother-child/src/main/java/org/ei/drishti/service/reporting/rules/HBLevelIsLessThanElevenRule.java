package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.util.DoubleUtil;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.HbTestFormFields.HB_LEVEL_FIELD;

@Component
public class HBLevelIsLessThanElevenRule implements IRule {

    public static final Double HB_LEVEL_THRESHOLD_FOR_ANEMIA = 11D;

    @Override
    public boolean apply(SafeMap reportFields) {
        double hbLevelValueFromField = DoubleUtil.tryParse(reportFields.get(HB_LEVEL_FIELD), 0D);
        return hbLevelValueFromField < HB_LEVEL_THRESHOLD_FOR_ANEMIA;
    }
}
