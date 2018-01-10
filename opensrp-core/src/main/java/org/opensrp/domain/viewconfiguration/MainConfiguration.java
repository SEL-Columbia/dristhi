package org.opensrp.domain.viewconfiguration;

import org.codehaus.jackson.annotate.JsonProperty;

public class MainConfiguration extends BaseConfiguration {
	
	@JsonProperty
	private boolean enableJsonViews;
	
	public boolean getEnableJsonViews() {
		return enableJsonViews;
	}
	
	public void setEnableJsonViews(boolean enableJsonViews) {
		this.enableJsonViews = enableJsonViews;
	}
	 
}
