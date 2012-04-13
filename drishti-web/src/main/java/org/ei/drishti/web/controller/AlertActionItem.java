package org.ei.drishti.web.controller;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ei.drishti.domain.AlertAction;
import org.ei.drishti.domain.AlertData;

import java.util.Map;

class AlertActionItem {
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private String caseID;
    @JsonProperty
    private Map<String, String> data;
    @JsonProperty
    private String alertType;
    @JsonProperty
    private long timeStamp;

    public AlertActionItem(String anmIdentifier, String caseID, Map<String, String> data, long timeStamp, String alertType) {
        this.anmIdentifier = anmIdentifier;
        this.caseID = caseID;
        this.data = data;
        this.timeStamp = timeStamp;
        this.alertType = alertType;
    }

    public static AlertActionItem from(AlertAction alertAction){
        return new AlertActionItem(alertAction.anmIdentifier(), alertAction.caseID(), alertAction.data(), alertAction.timestamp(), alertAction.alertType());
    }

    public AlertAction toAlertAction() {
        return new AlertAction(caseID, anmIdentifier, AlertData.from(alertType, data));
    }
}
