package org.opensrp.api.domain.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FormInstance {

	private String form_data_definition_version;

	private FormData form;

    public FormInstance() {
    }

    public FormInstance(FormData form) {
        this.form = form;
    }

    public String form_data_definition_version() {
		return form_data_definition_version;
	}

	public FormData form() {
        return form;
    }

    public String getField(String name) {
        return form.getField(name);
    }

    public SubFormData getSubFormByName(String name) {
        return form.getSubFormByName(name);
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
