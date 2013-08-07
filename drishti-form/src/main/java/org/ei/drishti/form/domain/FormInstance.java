package org.ei.drishti.form.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class FormInstance {
    @JsonProperty
    private String form_data_definition_version;

    @JsonProperty
    private FormData form;

    public FormInstance() {
    }

    public FormInstance(FormData form) {
        this.form = form;
    }

    public FormData form() {
        return form;
    }

    public String getField(String name) {
        return form.getField(name);
    }

    public SubFormData getSubFormByName(String name) {
        return form.getSubFormByName(name);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
