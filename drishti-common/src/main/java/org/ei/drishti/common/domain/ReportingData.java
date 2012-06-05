package org.ei.drishti.common.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ReportingData implements Serializable {
    private static final long serialVersionUID = 454645765753L;

    @JsonProperty
    private String type;
    @JsonProperty
    private Map<String, String> data;

    private ReportingData() {
    }

    public ReportingData(String type) {
        this.type = type;
        data = new HashMap<String, String>();
    }

    public ReportingData with(String key, String value) {
        data.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String get(String key) {
        return data.get(key);
    }
}
