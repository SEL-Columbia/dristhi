package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.util.IntegerUtil;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.FamilyPlanningFormFields.NUMBER_OF_ECPS_GIVEN_FIELD_NAME;

@Component
public class MoreThanZeroECPsSuppliedRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        return IntegerUtil.tryParse(reportFields.get(NUMBER_OF_ECPS_GIVEN_FIELD_NAME), 0) > 0;
    }
}

