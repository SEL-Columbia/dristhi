package org.opensrp.domain;

public class FormDefinition {

	private String form_data_definition_version="1";
	
	private Form form ;

	public String getForm_data_definition_version() {
		return form_data_definition_version;
	}

	public void setForm_data_definition_version(String form_data_definition_version) {
		this.form_data_definition_version = form_data_definition_version;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}
	
	
}
