package org.ei.drishti.dto;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class ActionItem {
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private String caseID;
    @JsonProperty
    private Map<String, String> data;
    @JsonProperty
    private String actionTarget;
    @JsonProperty
    private String actionType;
    @JsonProperty
    private long timeStamp;

    public ActionItem(String anmIdentifier, String caseID, Map<String, String> data, long timeStamp, String actionTarget, String actionType) {
        this.anmIdentifier = anmIdentifier;
        this.caseID = caseID;
        this.data = data;
        this.timeStamp = timeStamp;
        this.actionTarget = actionTarget;
        this.actionType = actionType;
    }

    public String caseID() {
        return caseID;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String actionType() {
        return actionType;
    }

    public String actionTarget() {
        return actionTarget;
    }

    public Map<String, String> data() {
        return data;
    }
}
