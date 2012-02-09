package org.ei.commcare;

import org.ei.commcare.listener.contract.CommCareExportUrl;
import org.ei.commcare.listener.contract.CommCareFormDefinition;
import org.ei.commcare.listener.domain.CommCareFormContent;
import org.ei.commcare.listener.domain.CommcareForm;

import java.util.HashMap;

public class CommCareFormBuilder {
    private String formName;
    private HashMap<String, String> mappings;
    private CommCareFormContent content;

    public CommCareFormBuilder() {
        this.mappings = new HashMap<String, String>();
    }

    public CommCareFormBuilder withName(String formName) {
        this.formName = formName;
        return this;
    }

    public CommCareFormBuilder withMapping(String pathToField, String parameterToBeMappedTo) {
        mappings.put(pathToField, parameterToBeMappedTo);
        return this;
    }

    public CommCareFormBuilder withContent(CommCareFormContent content) {
        this.content = content;
        return this;
    }

    public CommcareForm build() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nameSpace", "http://some.name/space");
        CommCareExportUrl url = new CommCareExportUrl("http://some.url", params);
        return new CommcareForm(new CommCareFormDefinition(formName, url, mappings), content);
    }
}
