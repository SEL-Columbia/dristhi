package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.*;
import static org.ei.drishti.common.AllConstants.SPACE;

@Component
public class IsOPV1ImmunizationGivenRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        String immunizationsGiven = reportFields.get(IMMUNIZATIONS_GIVEN_FIELD_NAME) != null ?
                reportFields.get(IMMUNIZATIONS_GIVEN_FIELD_NAME) : "";
        List<String> immunizationsGivenList = new ArrayList<>(asList(immunizationsGiven.split(SPACE)));

        if (reportFields.has(PREVIOUS_IMMUNIZATIONS_FIELD_NAME)) {
            String previousImmunizations = reportFields.get(PREVIOUS_IMMUNIZATIONS_FIELD_NAME) != null ?
                    reportFields.get(PREVIOUS_IMMUNIZATIONS_FIELD_NAME) : "";
            List<String> previousImmunizationsList = new ArrayList<>(asList(previousImmunizations.split(SPACE)));
            immunizationsGivenList.removeAll(previousImmunizationsList);
        }

        return immunizationsGivenList.contains(OPV_1_VALUE);
    }
}
