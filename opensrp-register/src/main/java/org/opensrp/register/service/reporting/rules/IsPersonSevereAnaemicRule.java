package org.opensrp.register.service.reporting.rules;

import org.opensrp.common.util.DoubleUtil;
import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.HbTestFormFields.HB_LEVEL_FIELD;

@Component
public class IsPersonSevereAnaemicRule implements IRule {

    public static final Double HB_LEVEL_HIGHER_THRESHOLD_FOR_ANAEMIA = 11D;
    public static final Double HB_LEVEL_LOWER_THRESHOLD_FOR_ANAEMIA = 7D;

    @Override
    public boolean apply(SafeMap reportFields) {
        double hbLevel = DoubleUtil.tryParse(reportFields.get(HB_LEVEL_FIELD), HB_LEVEL_HIGHER_THRESHOLD_FOR_ANAEMIA + 1);
        return hbLevel < HB_LEVEL_LOWER_THRESHOLD_FOR_ANAEMIA;
    }
}
