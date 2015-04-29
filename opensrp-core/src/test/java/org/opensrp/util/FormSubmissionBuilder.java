package org.opensrp.util;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.opensrp.form.domain.FormData;
import org.opensrp.form.domain.FormField;
import org.opensrp.form.domain.FormInstance;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.domain.SubFormData;

public class FormSubmissionBuilder {
    private String anmId = "anmId";
    private String instanceId = "instance id 1";
    private String entityId = "entity id 1";
    private String formName = "form name 1";
    private String bind_type = "entity 1";
    private String default_bind_path = "bind path 1";
    private String formDataDefinitionVersion = "1";
    private SubFormData subFormData = new SubFormData("sub form name", Collections.<Map<String, String>>emptyList());
    private List<FormField> fields = new ArrayList<>();
    private Long timestamp = 0L;
    private long serverVersion = 0L;

    public static FormSubmissionBuilder create() {
        return new FormSubmissionBuilder();
    }

    public FormSubmission build() {
        FormInstance formInstance = new FormInstance(new FormData(bind_type, default_bind_path, fields, asList(subFormData)));
        return new FormSubmission(anmId, instanceId, formName, entityId, timestamp, formDataDefinitionVersion, formInstance, serverVersion);
    }

    public FormSubmissionBuilder addFormField(String name, String value) {
        fields.add(new FormField(name, value, name));
        return this;
    }

    public FormSubmissionBuilder withTimeStamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public FormSubmissionBuilder withANMId(String anmId) {
        this.anmId = anmId;
        return this;
    }

    public FormSubmissionBuilder withInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    public FormSubmissionBuilder withEntityId(String entityId) {
        this.entityId = entityId;
        addFormField("id", entityId);
        return this;
    }

    public FormSubmissionBuilder withFormName(String formName) {
        this.formName = formName;
        return this;
    }

    public FormSubmissionBuilder withSubForm(SubFormData subFormData) {
        this.subFormData = subFormData;
        return this;
    }

    public FormSubmissionBuilder withServerVersion(long serverVersion) {
        this.serverVersion = serverVersion;
        return this;
    }
}
