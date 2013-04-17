package org.ei.drishti.dto.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormData {
    @JsonProperty
    private String bind_type;
    @JsonProperty
    private String default_bind_path;
    @JsonProperty
    private List<FormField> fields;

    private Map<String, String> mapOfFieldsByName;

    public FormData(String bind_type, String default_bind_path, List<FormField> fields) {
        this.bind_type = bind_type;
        this.default_bind_path = default_bind_path;
        this.fields = fields;
    }

    public List<FormField> fields() {
        return fields;
    }

    public String getField(String name) {
        if (mapOfFieldsByName == null) {
            createFieldMapByName();
        }
        return mapOfFieldsByName.get(name);
    }

    private void createFieldMapByName() {
        mapOfFieldsByName = new HashMap<String, String>();
        for (FormField field : fields) {
            mapOfFieldsByName.put(field.name(), field.value());
        }
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
