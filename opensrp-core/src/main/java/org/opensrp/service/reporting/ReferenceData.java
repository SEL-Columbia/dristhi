package org.opensrp.service.reporting;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class ReferenceData {
    private String type;
    private String idField;
    private List<String> fields;

    public ReferenceData(String type, String idField, List<String> fields) {
        this.type = type;
        this.idField = idField;
        this.fields = fields;
    }

    public String type() {
        return type;
    }

    public String idField() {
        return idField;
    }

    public List<String> fields() {
        return fields;
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
