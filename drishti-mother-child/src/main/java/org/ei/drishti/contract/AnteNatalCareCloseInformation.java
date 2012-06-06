package org.ei.drishti.contract;

public class AnteNatalCareCloseInformation {
    private String caseId;
    private String closeReason;

    public AnteNatalCareCloseInformation(String caseId, String closeReason) {
        this.caseId = caseId;
        this.closeReason = closeReason;
    }

    public String caseId() {
        return caseId;
    }

    public String reason() {
        return closeReason;
    }

    @Override
    public String toString() {
        return "AnteNatalCareCloseInformation{caseId='" + caseId + '\'' + '}';
    }
}
