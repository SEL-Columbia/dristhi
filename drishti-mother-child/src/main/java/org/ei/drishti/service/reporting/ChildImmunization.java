package org.ei.drishti.service.reporting;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.util.SafeMap;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class ChildImmunization {
    public boolean isImmunizedWith(String immunisation, SafeMap reportFields) {
        String immunizationsGiven = reportFields.get(AllConstants.ChildImmunizationFields.IMMUNIZATIONS_GIVEN_FIELD_NAME) != null ?
                reportFields.get(AllConstants.ChildImmunizationFields.IMMUNIZATIONS_GIVEN_FIELD_NAME) : "";
        List<String> immunizationsGivenList = new ArrayList<>(asList(immunizationsGiven.split(AllConstants.SPACE)));

        if (reportFields.has(AllConstants.ChildImmunizationFields.PREVIOUS_IMMUNIZATIONS_FIELD_NAME)) {
            String previousImmunizations = reportFields.get(AllConstants.ChildImmunizationFields.PREVIOUS_IMMUNIZATIONS_FIELD_NAME) != null ?
                    reportFields.get(AllConstants.ChildImmunizationFields.PREVIOUS_IMMUNIZATIONS_FIELD_NAME) : "";
            List<String> previousImmunizationsList = new ArrayList<>(asList(previousImmunizations.split(AllConstants.SPACE)));
            immunizationsGivenList.removeAll(previousImmunizationsList);
        }

        return immunizationsGivenList.contains(immunisation);
    }
}