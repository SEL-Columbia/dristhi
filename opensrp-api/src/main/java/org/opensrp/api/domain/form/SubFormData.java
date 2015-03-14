package org.opensrp.api.domain.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SubFormData {
    
    private String name;

    private String bind_type;

    private String default_bind_path;
    
    private List<FormField> fields;

    private List<Map<String, String>> instances;

    public SubFormData() {
        this.instances =  new ArrayList<>();
        this.name = "";
    }

    public SubFormData(String name, List<Map<String, String>> instances) {
        this.instances = instances;
        this.name = name;
    }

    public String name() {
        return name;
    }

    public List<Map<String, String>> instances() {
        return instances;
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
