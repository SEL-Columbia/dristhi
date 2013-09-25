package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.util.DoubleUtil;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.HbTestFormFields.HB_LEVEL_FIELD;

@Component
public class IsPersonAnaemicRule implements IRule {

    public static final Double HB_LEVEL_THRESHOLD_FOR_ANAEMIA = 11D;

    @Override
    public boolean apply(SafeMap reportFields) {
        double hbLevel = DoubleUtil.tryParse(reportFields.get(HB_LEVEL_FIELD), HB_LEVEL_THRESHOLD_FOR_ANAEMIA + 1);
        return hbLevel < HB_LEVEL_THRESHOLD_FOR_ANAEMIA;
    }
}
