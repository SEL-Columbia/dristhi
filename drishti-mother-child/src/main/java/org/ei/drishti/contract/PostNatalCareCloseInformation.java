package org.ei.drishti.contract;

public class PostNatalCareCloseInformation {
    public String caseId;
    public String anmIdentifier;

    public PostNatalCareCloseInformation(String caseId, String anmIdentifier) {
        this.caseId = caseId;
        this.anmIdentifier = anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }
}
