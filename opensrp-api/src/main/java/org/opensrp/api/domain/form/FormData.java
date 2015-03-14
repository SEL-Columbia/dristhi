package org.opensrp.api.domain.form;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormData {
    private String bind_type;

    private String default_bind_path;

    private List<FormField> fields;

    private List<SubFormData> sub_forms;

    private Map<String, String> mapOfFieldsByName;

    public FormData() {
    }

    public FormData(String bind_type, String default_bind_path, List<FormField> fields, List<SubFormData> sub_forms) {
        this.bind_type = bind_type;
        this.default_bind_path = default_bind_path;
        this.fields = fields;
        this.sub_forms = sub_forms;
    }

    public String getBind_type() {
		return bind_type;
	}

	public void setBind_type(String bind_type) {
		this.bind_type = bind_type;
	}

	public String getDefault_bind_path() {
		return default_bind_path;
	}

	public void setDefault_bind_path(String default_bind_path) {
		this.default_bind_path = default_bind_path;
	}

	public List<FormField> getFields() {
		return fields;
	}

	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}

	public List<SubFormData> getSub_forms() {
		return sub_forms;
	}

	public void setSub_forms(List<SubFormData> sub_forms) {
		this.sub_forms = sub_forms;
	}

	public Map<String, String> getMapOfFieldsByName() {
		return mapOfFieldsByName;
	}

	public void setMapOfFieldsByName(Map<String, String> mapOfFieldsByName) {
		this.mapOfFieldsByName = mapOfFieldsByName;
	}

	public String getField(String name) {
        if (mapOfFieldsByName == null) {
            createFieldMapByName();
        }
        return mapOfFieldsByName.get(name);
    }

    private void createFieldMapByName() {
        mapOfFieldsByName = new HashMap<>();
        for (FormField field : fields) {
            mapOfFieldsByName.put(field.name(), field.value());
        }
    }

    public SubFormData getSubFormByName(String name) {
        for (SubFormData sub_form : sub_forms) {
            if (StringUtils.equalsIgnoreCase(name, sub_form.name()))
                return sub_form;
        }
        throw new RuntimeException(MessageFormat.format("No sub form with the given name: {0}, in formData: {1}", name, this));
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
