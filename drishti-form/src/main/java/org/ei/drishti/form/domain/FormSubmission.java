package org.ei.drishti.form.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'FormSubmission'")
public class FormSubmission extends MotechBaseDataObject {
    @JsonProperty
    private String instanceId;
    @JsonProperty
    private String formName;
    @JsonProperty
    private String anmId;
    @JsonProperty
    private long timestamp;
    @JsonProperty
    private String entityId;
    @JsonProperty
    private FormInstance formInstance;
    @JsonProperty
    private long serverVersion;

    public FormSubmission() {
    }

    public FormSubmission(String anmId, String instanceId, String formName, String entityId, FormInstance formInstance, long timestamp, long serverVersion) {
        this.instanceId = instanceId;
        this.formName = formName;
        this.anmId = anmId;
        this.timestamp = timestamp;
        this.entityId = entityId;
        this.formInstance = formInstance;
        this.serverVersion = serverVersion;
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

    public FormInstance instance() {
        return formInstance;
    }

    public long timestamp() {
        return this.timestamp;
    }

    public long serverVersion() {
        return serverVersion;
    }

    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(o, this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
