package org.ei.drishti.dto.form;

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
    private long timeStamp;

    public FormSubmission(String anmId, String instanceId, String entityId, String formName, FormInstance formInstance, long timeStamp) {
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

    public long timeStamp() {
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
}
