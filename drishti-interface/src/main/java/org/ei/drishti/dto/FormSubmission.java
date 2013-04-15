package org.ei.drishti.dto;

import org.codehaus.jackson.annotate.JsonProperty;

public class FormSubmission {
    @JsonProperty
    private String anmId;
    @JsonProperty
    private String instanceId;
    @JsonProperty
    private String entityId;
    @JsonProperty
    private String formName;
    @JsonProperty
    private String formInstance;
    @JsonProperty
    private long timeStamp;

    public FormSubmission(String anmId, String instanceId, String entityId, String formName, String formInstance, long timeStamp) {
        this.anmId = anmId;
        this.instanceId = instanceId;
        this.entityId = entityId;
        this.formName = formName;
        this.formInstance = formInstance;
        this.timeStamp = timeStamp;
    }

    public String anmId() {
        return this.anmId;
    }

    public String instanceId() {
        return this.instanceId;
    }

    public String entityId() {
        return this.entityId;
    }

    public String formName() {
        return this.formName;
    }

    public String instance() {
        return formInstance;
    }

    public long timeStamp() {
        return this.timeStamp;
    }
}
