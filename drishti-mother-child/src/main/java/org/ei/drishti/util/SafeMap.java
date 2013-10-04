package org.ei.drishti.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

public class SafeMap {
    private Map<String, String> data;

    public SafeMap() {
        this(new HashMap<String, String>());
    }

    public SafeMap(Map<String, String> data) {
        this.data = data;
    }

    public SafeMap putAll(SafeMap formFieldsMap) {
        for (String key : formFieldsMap.data.keySet()) {
            put(key, formFieldsMap.get(key));
        }
        return this;
    }

    public String get(String key) {
        if (!data.containsKey(key)) {
            throw new RuntimeException("Key: " + key + " does not exist in: " + this);
        }
        return data.get(key);
    }

    public SafeMap put(String key, String value) {
        data.put(key, value);
        return this;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public boolean has(String key) {
        return data.containsKey(key);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
