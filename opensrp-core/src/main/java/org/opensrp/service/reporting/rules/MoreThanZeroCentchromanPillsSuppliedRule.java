package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.NUMBER_OF_CENTCHROMAN_PILLS_SUPPLIED_FIELD_NAME;

import org.opensrp.common.util.IntegerUtil;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class MoreThanZeroCentchromanPillsSuppliedRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return IntegerUtil.tryParse(reportFields.get(NUMBER_OF_CENTCHROMAN_PILLS_SUPPLIED_FIELD_NAME), 0) > 0;
    }
}

