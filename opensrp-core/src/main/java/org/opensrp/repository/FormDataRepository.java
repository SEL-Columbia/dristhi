package org.opensrp.repository;

public abstract class FormDataRepository {
	//public abstract String saveFormSubmission(String params, String data, String formDataDefinitionVersion);
	public abstract String saveEntity(String entityType, String fields);
}
