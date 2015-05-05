package org.opensrp.register.service.reporting.rules;

import org.opensrp.service.reporting.rules.IRule;
import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.PNCVisitFormFields.HEAVY_BLEEDING_VALUE;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VAGINAL_PROBLEMS_FIELD_NAME;

@Component
public class IsMotherDoesNotHaveHemorrhageRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {

        String vaginalProblemValue = reportFields.get(VAGINAL_PROBLEMS_FIELD_NAME) != null ?
                reportFields.get(VAGINAL_PROBLEMS_FIELD_NAME) : "";

        return !vaginalProblemValue.contains(HEAVY_BLEEDING_VALUE);
    }
}
