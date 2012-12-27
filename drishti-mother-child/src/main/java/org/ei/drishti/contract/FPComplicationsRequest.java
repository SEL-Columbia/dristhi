package org.ei.drishti.contract;

public class FPComplicationsRequest {
    private String caseId;
    private String anmIdentifier;

    public FPComplicationsRequest(String caseId, String anmIdentifier) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }
}
