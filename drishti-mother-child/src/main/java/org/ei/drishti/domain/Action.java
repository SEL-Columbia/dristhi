package org.ei.drishti.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ei.drishti.dto.ActionData;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.DateUtil;

import java.util.Map;

@TypeDiscriminator("doc.type === 'Action'")
@JsonIgnoreProperties({"actionActive"})
public class Action extends MotechBaseDataObject {
    @JsonProperty
    private String anmIdentifier;
    @JsonProperty
    private String caseId;
    @JsonProperty
    private Map<String, String> data;
    @JsonProperty
    private String actionTarget;
    @JsonProperty
    private String actionType;
    @JsonProperty
    private Boolean isActionActive;
    @JsonProperty
    private long timeStamp;
    @JsonProperty
    private Map<String, String> details;

    private Action() {
    }

    public Action(String caseId, String anmIdentifier, ActionData actionData) {
        this.anmIdentifier = anmIdentifier;
        this.caseId = caseId;
        this.data = actionData.data();
        this.actionTarget = actionData.target();
        this.actionType = actionData.type();
        this.timeStamp = DateUtil.now().getMillis();
        this.details = actionData.details();
        this.isActionActive = true;
    }

    public String anmIdentifier() {
        return anmIdentifier;
    }

    public String caseId() {
        return caseId;
    }

    public Map<String, String> data() {
        return data;
    }

    public String actionType() {
        return actionType;
    }

    public long timestamp() {
        return timeStamp;
    }

    public String target() {
        return actionTarget;
    }

    public Action markAsInActive() {
        this.isActionActive = false;
        return this;
    }

    public Boolean isActionActive() {
        return isActionActive;
    }

    public Map<String, String> details() {
        return details;
    }

    private String getCaseId() {
        return caseId;
    }

    public String getActionTarget() {
        return actionTarget;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, false, null, new String[]{"timeStamp", "revision"});
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
