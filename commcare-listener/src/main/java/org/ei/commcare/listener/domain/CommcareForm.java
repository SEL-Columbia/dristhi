package org.ei.commcare.listener.domain;

import org.ei.commcare.listener.contract.CommcareFormDefinition;

public class CommcareForm {
    private CommcareFormDefinition formDefinition;
    private String content;

    public CommcareForm(CommcareFormDefinition formDefinition, String content) {
        this.formDefinition = formDefinition;
        this.content = content;
    }

    public CommcareFormDefinition definition() {
        return formDefinition;
    }

    public String content() {
        return content;
    }
}
