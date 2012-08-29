package org.ei.drishti.contract;

public class UpdateDetailsRequest {
    private String caseId;
    private String anmIdentifier;

    public UpdateDetailsRequest(String caseId, String anmIdentifier) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }
}
