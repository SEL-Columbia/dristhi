package org.opensrp.form.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubFormData {
    @JsonProperty
    private String name;
    @JsonProperty
    private String bind_type;
    @JsonProperty
    private String default_bind_path;
    @JsonProperty
    private List<FormField> fields;
    @JsonProperty
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
    
    public String defaultBindPath() {
        return default_bind_path;
    }
    
    public String bindType() {
        return bind_type;
    }
    
    public List<FormField> fields() {
        return fields;
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
