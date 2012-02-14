package org.ei.commcare.api.domain;

import org.ei.commcare.api.contract.CommCareFormDefinition;

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

    public CommCareFormContent content() {
        return content;
    }
}
