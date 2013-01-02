package org.ei.drishti.contract;

public class AnteNatalCareInformationSubset {
    private String caseId;
    private String anmIdentifier;

    public AnteNatalCareInformationSubset(String caseId, String anmIdentifier) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }
}
