package org.ei.drishti.common.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class ReportingData {
    @JsonProperty
    private String type;
    @JsonProperty
    private Map<String, String> data;

    private ReportingData() {
    }

    public ReportingData(String type) {
        this.type = type;
    }

    public ReportingData with(String key, String value) {
        data.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
