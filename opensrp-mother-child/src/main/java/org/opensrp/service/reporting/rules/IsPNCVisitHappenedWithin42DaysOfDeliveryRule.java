package org.opensrp.service.reporting.rules;

import org.opensrp.common.util.IntegerUtil;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.PNCVisitFormFields.VISIT_DAY_FIELD_NAME;

@Component
public class IsPNCVisitHappenedWithin42DaysOfDeliveryRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return IntegerUtil.tryParse(reportFields.get(VISIT_DAY_FIELD_NAME), 100) <= 42;
    }
}
