package org.ei.commcare;

import org.ei.commcare.contract.CommcareExportUrl;
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

    public CommcareFormBuilder withMapping(String parameterToBeMappedTo, String pathToField) {
        mappings.put(parameterToBeMappedTo, pathToField);
        return this;
    }

    public CommcareFormBuilder withContent(String content) {
        this.content = content;
        return this;
    }

    public CommcareForm build() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nameSpace", "http://some.name/space");
        CommcareExportUrl url = new CommcareExportUrl("http://some.url", params);
        return new CommcareForm(new CommcareFormDefinition(formName, url, mappings), content);
    }
}
