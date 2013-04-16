package org.ei.drishti.dto.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private FormInstance formInstance;
    @JsonProperty
    private String timeStamp;

    public FormSubmission(String anmId, String instanceId, String entityId, String formName, FormInstance formInstance, String timeStamp) {
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

    public FormInstance instance() {
        return formInstance;
    }

    public String timeStamp() {
        return this.timeStamp;
    }

    public List<FormField> fields() {
        return formInstance.form().fields();
    }

    public String getField(String name) {
        return formInstance.getField(name);
    }

    public Map<String, String> getFields(List<String> fieldNames) {
        Map<String, String> fieldsMap = new HashMap<String, String>();
        for (String fieldName : fieldNames) {
            fieldsMap.put(fieldName, getField(fieldName));
        }
        return fieldsMap;
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
