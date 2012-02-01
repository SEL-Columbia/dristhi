package org.ei.commcare.domain;

import java.util.List;

public class CommcareForm {
    private CommcareFormDefinition formDefinition;
    private byte[] formContent;
    private List<String> formInstances;

    public CommcareForm(CommcareFormDefinition formDefinition, List<String> formInstances) {
        this.formDefinition = formDefinition;
        this.formInstances = formInstances;
    }

    public List<String> instances() {
        return formInstances;
    }

    public CommcareFormDefinition definition() {
        return formDefinition;
    }
}
