package org.opensrp.api.domain.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FormField {

	private String name;

	private String value;
    
    private String source;

    public FormField() {
    }

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
    
    public String source() {
        return source;
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
