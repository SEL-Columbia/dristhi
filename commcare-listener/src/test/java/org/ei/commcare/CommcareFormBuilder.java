package org.ei.commcare;

import org.ei.commcare.contract.CommcareFormDefinition;
import org.ei.commcare.domain.CommcareForm;

import java.util.HashMap;

public class CommcareFormBuilder {
    private String formName;
    private HashMap<String, String> mappings;
    private String content;

    public CommcareFormBuilder() {
        this.mappings = new HashMap<String, String>();
    }

    public CommcareFormBuilder withName(String formName) {
        this.formName = formName;
        return this;
    }

    public CommcareFormBuilder withMappings(String fieldName, String parameterToBeMappedTo) {
        mappings.put(fieldName, parameterToBeMappedTo);
        return this;
    }

    public CommcareFormBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public CommcareForm build() {
        return new CommcareForm(new CommcareFormDefinition(formName, null, mappings), content);
    }
}
