package org.ei.drishti.contract;

public class PostNatalCareCloseInformation {
    private String caseId;
    private String anmIdentifier;
    private String closeReason;

    public PostNatalCareCloseInformation(String caseId, String anmIdentifier, String closeReason) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
        this.closeReason = closeReason;
    }

    public String caseId() {
        return caseId;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String closeReason() {
        return closeReason;
    }
}
