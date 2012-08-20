package org.ei.drishti.contract;

import java.util.Date;

public class AnteNatalCareOutcomeInformation {
    private String caseId;
    private Date dateOfDelivery;
    private String pregnancyOutcome;
    private boolean hasDeliveryComplications;
    private String deliveryComplications;
    private boolean isHighRisk;
    private String highRiskReason;

    @Override
    public String toString() {
        return "AnteNatalCareOutcomeInformation{caseId='" + caseId + '\'' + ", dateOfDelivery=" + dateOfDelivery + '}';
    }
}
