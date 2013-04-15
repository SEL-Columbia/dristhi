package org.ei.drishti.util;

import org.ei.drishti.dto.form.FormData;
import org.ei.drishti.dto.form.FormField;
import org.ei.drishti.dto.form.FormInstance;
import org.ei.drishti.dto.form.FormSubmission;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class FormSubmissionBuilder {

    private String anmId = "anmId";
    private String instanceId = "instance id 1";
    private String entityId = "entity id 1";
    private String formName = "form name 1";
    private String bind_type = "entity 1";
    private String default_bind_path = "bind path 1";
    private List<FormField> fields = new ArrayList<>();
    private FormInstance formInstance = new FormInstance(new FormData(bind_type, default_bind_path, fields));
    private long timestamp = DateUtil.now().getMillis();

    public static FormSubmissionBuilder create() {
        return new FormSubmissionBuilder();
    }

    public FormSubmission build() {
        return new FormSubmission(anmId, instanceId, entityId, formName, formInstance, timestamp);
    }

    public FormSubmissionBuilder addFormField(String name, String value) {
        fields.add(new FormField(name, value, name));
        return this;
    }

    public FormSubmissionBuilder withTimeStamp(long timestamp) {
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
        return this;
    }

    public FormSubmissionBuilder withFormName(String formName) {
        this.formName = formName;
        return this;
    }
}
