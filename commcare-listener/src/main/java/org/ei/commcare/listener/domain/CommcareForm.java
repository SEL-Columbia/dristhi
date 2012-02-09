package org.ei.commcare.listener.domain;

import org.ei.commcare.listener.contract.CommcareFormDefinition;
import org.ei.commcare.listener.util.CommCareFormContent;

public class CommcareForm {
    private CommcareFormDefinition formDefinition;
    private CommCareFormContent content;

    public CommcareForm(CommcareFormDefinition formDefinition, CommCareFormContent content) {
        this.formDefinition = formDefinition;
        this.content = content;
    }

    public CommcareFormDefinition definition() {
        return formDefinition;
    }

    public CommCareFormContent content() {
        return content;
    }
}
