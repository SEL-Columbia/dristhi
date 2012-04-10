package org.ei.drishti.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class AlertData {
    private final HashMap<String, String> data;
    private String type;

    public static AlertData create(String motherName, String thaayiCardNumber, String visitCode, String latenessStatus, DateTime dueDate) {
        return new AlertData("create").with("motherName", motherName).with("thaayiCardNumber", thaayiCardNumber)
                .with("visitCode", visitCode).with("latenessStatus", latenessStatus).with("dueDate", dueDate.toLocalDate().toString());
    }

    private AlertData(String type) {
        this.type = type;
        data = new HashMap<String, String>();
    }

    public AlertData with(String key, String value) {
        data.put(key, value);
        return this;
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

    public Map<String, String> data() {
        return data;
    }

    public String type() {
        return type;
    }
}
