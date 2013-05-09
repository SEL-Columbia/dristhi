package org.ei.drishti.domain.form;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<FormField> fields() {
        return formInstance.form().fields();
    }

    public String getField(String name) {
        return formInstance.getField(name);
    }

    public String getInstanceId() {
        return instanceId;
    }

    public Map<String, String> getFields(List<String> fieldNames) {
        Map<String, String> fieldsMap = new HashMap<>();
        for (String fieldName : fieldNames) {
            fieldsMap.put(fieldName, getField(fieldName));
        }
        return fieldsMap;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(o, this, false, getClass());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(17, 37, this, false, getClass());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
