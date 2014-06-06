package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.util.IntegerUtil;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCFormFields.ANC_VISIT_NUMBER_FIELD;

@Component
public class IsSecondANCVisitRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return IntegerUtil.tryParse(reportFields.get(ANC_VISIT_NUMBER_FIELD), 0) == 2;
    }

}
