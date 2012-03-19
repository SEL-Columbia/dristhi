package org.ei.commcare.api.domain;

import org.ei.commcare.api.contract.CommCareFormDefinition;

import java.util.Map;

public class CommcareFormInstance {
    private CommCareFormDefinition formDefinition;
    private CommCareFormContent content;

    public CommcareFormInstance(CommCareFormDefinition formDefinition, CommCareFormContent content) {
        this.formDefinition = formDefinition;
        this.content = content;
    }

    public CommCareFormDefinition definition() {
        return formDefinition;
    }

    public Map<String, String> content() {
        return content.getValuesOfFieldsSpecifiedByPath(formDefinition.mappings());
    }

    public String formId() {
        return content.formId();
    }
}
