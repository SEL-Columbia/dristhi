package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.ANCFormFields.ANC_VISIT_NUMBER_FIELD;

import org.opensrp.common.util.IntegerUtil;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsThirdANCVisitRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return IntegerUtil.tryParse(reportFields.get(ANC_VISIT_NUMBER_FIELD), 0) == 3;
    }

}
