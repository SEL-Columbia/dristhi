package org.ei.drishti.service.reporting;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.util.SafeMap;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.*;

public class ChildImmunization {
    public boolean isImmunizedWith(String immunization, SafeMap reportFields) {
        List<String> immunizationsGivenList = getImmunizationGivenList(reportFields);
        immunizationsGivenList.removeAll(getPreviousImmunizations(reportFields));

        return immunizationsGivenList.contains(immunization);
    }

    private List<String> getImmunizationGivenList(SafeMap reportFields) {
        String immunizationsGiven = reportFields.get(IMMUNIZATIONS_GIVEN_FIELD_NAME) != null ?
                reportFields.get(IMMUNIZATIONS_GIVEN_FIELD_NAME) : "";
        return new ArrayList<>(asList(immunizationsGiven.split(AllConstants.SPACE)));
    }

    public boolean isAllImmunizationsGiven(SafeMap reportFields) {
        List<String> immunizationsGivenList = getImmunizationGivenList(reportFields);
        return immunizationsGivenList.containsAll(IMMUNIZATIONS_VALUE_LIST);
    }

    public boolean isImmunizationsGivenWithMeaslesOrMMR(SafeMap reportFields) {
        List<String> immunizationsGivenList = getImmunizationGivenList(reportFields);
        if (immunizationsGivenList.contains(AllConstants.ChildImmunizationFields.MMR_VALUE))
            return immunizationsGivenList.containsAll(IMMUNIZATIONS_WITH_MMR_VALUE_LIST);
        return immunizationsGivenList.containsAll(IMMUNIZATIONS_VALUE_LIST);
    }

    public boolean isPreviouslyImmunizedWith(String immunization, SafeMap reportFields) {
        return getPreviousImmunizations(reportFields).contains(immunization);
    }

    private List<String> getPreviousImmunizations(SafeMap reportFields) {
        List<String> previousImmunizationsList = new ArrayList<>();
        if (reportFields.has(PREVIOUS_IMMUNIZATIONS_FIELD_NAME)) {
            String previousImmunizations = reportFields.get(PREVIOUS_IMMUNIZATIONS_FIELD_NAME) != null ?
                    reportFields.get(PREVIOUS_IMMUNIZATIONS_FIELD_NAME) : "";
            previousImmunizationsList = new ArrayList<>(asList(previousImmunizations.split(AllConstants.SPACE)));
        }
        return previousImmunizationsList;
    }
}