package org.ei.drishti.contract;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AnteNatalCareOutcomeInformation {
    private String caseId;
    private String anmIdentifier;

    public AnteNatalCareOutcomeInformation(String caseId, String anmIdentifier) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
