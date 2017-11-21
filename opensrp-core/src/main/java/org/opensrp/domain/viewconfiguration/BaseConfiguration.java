package org.opensrp.domain.viewconfiguration;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = LoginConfiguration.class, name = "Login"),
	@Type(value = MainConfiguration.class, name = "Main")
        @Type(value = RegisterConfiguration.class, name = "Register") })
public abstract class BaseConfiguration {
	
	@JsonProperty
	private String language;
	
	@JsonProperty
	private String applicationName;
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getApplicationName() {
		return applicationName;
	}
	
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
}
