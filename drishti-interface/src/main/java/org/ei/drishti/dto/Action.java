package org.ei.drishti.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class Action {
    @JsonProperty
    private String caseID;
    @JsonProperty
    private Map<String, String> data;
    @JsonProperty
    private String actionTarget;
    @JsonProperty
    private String actionType;
    @JsonProperty
    private String timeStamp;
    @JsonProperty
    private boolean isActionActive;
    @JsonProperty
    private Map<String, String> details;

    public Action(String caseID, String actionTarget, String actionType, Map<String, String> data, String timeStamp, boolean isActionActive, Map<String, String> details) {
        this.caseID = caseID;
        this.data = data;
        this.timeStamp = timeStamp;
        this.actionTarget = actionTarget;
        this.actionType = actionType;
        this.isActionActive = isActionActive;
        this.details = details;
    }

    public Action() {
    }

    public String caseID() {
        return caseID;
    }

    public String target() {
        return actionTarget;
    }

    public String type() {
        return actionType;
    }

    public boolean isActionActive() {
        return isActionActive;
    }

    public String index() {
        return timeStamp;
    }

    public String get(String key) {
        return data.get(key);
    }

    public Map<String, String> data() {
        return data;
    }

    public Map<String, String> details() {
        return details;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
