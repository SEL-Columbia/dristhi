package org.ei.drishti.form.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ei.drishti.form.domain.FormData;

public class FormInstance {
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
