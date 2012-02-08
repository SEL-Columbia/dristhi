package org.ei.commcare;

import org.ei.commcare.listener.contract.CommCareExportUrl;
import org.ei.commcare.listener.contract.CommcareFormDefinition;
import org.ei.commcare.listener.domain.CommcareForm;
import org.ei.commcare.listener.util.CommCareJsonFormContent;

import java.util.HashMap;

public class CommcareFormBuilder {
    private String formName;
    private HashMap<String, String> mappings;
    private CommCareJsonFormContent content;

    public CommcareFormBuilder() {
        this.mappings = new HashMap<String, String>();
    }

    public CommcareFormBuilder withName(String formName) {
        this.formName = formName;
        return this;
    }

    public CommcareFormBuilder withMapping(String pathToField, String parameterToBeMappedTo) {
        mappings.put(pathToField, parameterToBeMappedTo);
        return this;
    }

    public CommcareFormBuilder withContent(CommCareJsonFormContent content) {
        this.content = content;
        return this;
    }

    public CommcareForm build() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nameSpace", "http://some.name/space");
        CommCareExportUrl url = new CommCareExportUrl("http://some.url", params);
        return new CommcareForm(new CommcareFormDefinition(formName, url, mappings), content);
    }
}
