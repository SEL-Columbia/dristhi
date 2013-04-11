package org.ei.drishti.dto;

public class FormSubmission {
    private final String anmId;
    private final String instanceId;
    private final String entityId;
    private final String formName;
    private final String formInstance;
    private final long timeStamp;

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
