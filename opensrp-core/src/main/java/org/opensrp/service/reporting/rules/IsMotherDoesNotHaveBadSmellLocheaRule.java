package org.opensrp.service.reporting.rules;

import static org.opensrp.common.AllConstants.PNCVisitFormFields.BAD_SMELL_LOCHEA_VALUE;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VAGINAL_PROBLEMS_FIELD_NAME;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsMotherDoesNotHaveBadSmellLocheaRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        String vaginalProblemsValue = reportFields.get(VAGINAL_PROBLEMS_FIELD_NAME) != null
                ? reportFields.get(VAGINAL_PROBLEMS_FIELD_NAME) : "";

        return !vaginalProblemsValue.contains(BAD_SMELL_LOCHEA_VALUE);
    }
}
