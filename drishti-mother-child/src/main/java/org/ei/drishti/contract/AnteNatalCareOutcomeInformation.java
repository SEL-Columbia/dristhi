package org.ei.drishti.contract;

import java.util.Date;

public class AnteNatalCareOutcomeInformation {
    private String caseId;
    private Date dateOfDelivery;

    @Override
    public String toString() {
        return "AnteNatalCareOutcomeInformation{caseId='" + caseId + '\'' + ", dateOfDelivery=" + dateOfDelivery + '}';
    }
}
