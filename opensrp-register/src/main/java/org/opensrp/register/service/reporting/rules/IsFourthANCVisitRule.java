package org.opensrp.register.service.reporting.rules;

import org.opensrp.common.util.IntegerUtil;
import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.ANCFormFields.ANC_VISIT_NUMBER_FIELD;

@Component
public class IsFourthANCVisitRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return IntegerUtil.tryParse(reportFields.get(ANC_VISIT_NUMBER_FIELD), 0) == 4;
    }

}
