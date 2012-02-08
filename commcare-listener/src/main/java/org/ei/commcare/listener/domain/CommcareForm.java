package org.ei.commcare.listener.domain;

import org.ei.commcare.listener.contract.CommcareFormDefinition;
import org.ei.commcare.listener.util.CommCareJsonFormContent;

public class CommcareForm {
    private CommcareFormDefinition formDefinition;
    private CommCareJsonFormContent content;

    public CommcareForm(CommcareFormDefinition formDefinition, CommCareJsonFormContent content) {
        this.formDefinition = formDefinition;
        this.content = content;
    }

    public CommcareFormDefinition definition() {
        return formDefinition;
    }

    public CommCareJsonFormContent content() {
        return content;
    }
}
