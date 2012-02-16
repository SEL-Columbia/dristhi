package org.ei.drishti.contract;

import java.util.Date;

public class AnteNatalCareCloseInformation {
    private String caseId;
    private Date dateOfDelivery;

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
