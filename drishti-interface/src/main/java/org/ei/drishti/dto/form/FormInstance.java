package org.ei.drishti.dto.form;

import org.codehaus.jackson.annotate.JsonProperty;

public class FormInstance {
    @JsonProperty
    private FormData form;

    public FormInstance(FormData form) {
        this.form = form;
    }

    public FormData form() {
        return form;
    }

    public String getField(String name) {
        return form.getField(name);
    }
}
