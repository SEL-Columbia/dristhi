package org.ei.drishti.contract;

public class AnteNatalCareCloseInformation {
    private String caseId;

    public AnteNatalCareCloseInformation(String caseId) {
        this.caseId = caseId;
    }

    public String caseId() {
        return caseId;
    }

    @Override
    public String toString() {
        return "AnteNatalCareCloseInformation{caseId='" + caseId + '\'' + '}';
    }
}
