package org.ei.drishti.dto.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class FormSubmissionDTO {
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
    private String clientVersion;
    @JsonProperty
    private String serverVersion;
    @JsonProperty
    private String formDataDefinitionVersion;
    @JsonProperty
    private String village;

    public FormSubmissionDTO(String anmId, String instanceId, String entityId, String formName, String formInstance, String clientVersion, String formDataDefinitionVersion, String village) {
        this.anmId = anmId;
        this.instanceId = instanceId;
        this.entityId = entityId;
        this.formName = formName;
        this.formInstance = formInstance;
        this.clientVersion = clientVersion;
        this.formDataDefinitionVersion = formDataDefinitionVersion;
        this.village = village;
    }

    public FormSubmissionDTO withServerVersion(String version) {
        this.serverVersion = version;
        return this;
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

    public String clientVersion() {
        return this.clientVersion;
    }

    public String  formDataDefinitionVersion() {
        return this.formDataDefinitionVersion;
    }
    public String  village() {
        return this.village;
    }

    public String serverVersion() {
        return serverVersion;
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
