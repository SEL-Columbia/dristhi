package org.ei.drishti.dto.form;

import org.codehaus.jackson.annotate.JsonProperty;

public class FormField {
    @JsonProperty
    private String name;
    @JsonProperty
    private String value;
    @JsonProperty
    private String source;

    public FormField(String name, String value, String source) {
        this.name = name;
        this.value = value;
        this.source = source;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }
}
