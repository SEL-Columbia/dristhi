package org.ei.drishti.web.controller;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ei.drishti.domain.Action;
import org.ei.drishti.domain.ActionData;

import java.util.Map;

class ActionItem {
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

    public static ActionItem from(Action action){
        return new ActionItem(action.anmIdentifier(), action.caseID(), action.data(), action.timestamp(), action.target(), action.actionType());
    }

    public Action toAction() {
        return new Action(caseID, anmIdentifier, ActionData.from(actionType, actionTarget, data));
    }
}
