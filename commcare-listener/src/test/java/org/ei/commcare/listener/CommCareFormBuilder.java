package org.ei.commcare.listener;

import org.ei.commcare.api.contract.CommCareExportUrl;
import org.ei.commcare.api.contract.CommCareFormDefinition;
import org.ei.commcare.api.domain.CommCareFormContent;
import org.ei.commcare.api.domain.CommcareFormInstance;

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

    public CommcareFormInstance build() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("nameSpace", "http://some.name/space");
        CommCareExportUrl url = new CommCareExportUrl("http://some.url", params);
        return new CommcareFormInstance(new CommCareFormDefinition(formName, url, mappings), content);
    }
}
