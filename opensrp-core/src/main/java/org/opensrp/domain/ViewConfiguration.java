package org.opensrp.domain;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

/**
 * 
 * @author SGithengi
 *
 */
@TypeDiscriminator("doc.type == 'ViewConfiguration'")
public class ViewConfiguration extends BaseDataObject {

	private static final long serialVersionUID = 1890883609898207737L;
	
	@JsonProperty
	private String type;
	
	@JsonProperty
	private String identifier;
	
	@JsonProperty
	private String configurationType;
	
	@JsonProperty
	private Map<String, String> metadata;
	
	@JsonProperty
	private List<View> views;
	
	@JsonProperty
	private Map<String, String> labels;


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getConfigurationType() {
		return configurationType;
	}

	public void setConfigurationType(String configurationType) {
		this.configurationType = configurationType;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	public List<View> getViews() {
		return views;
	}

	public void setViews(List<View> views) {
		this.views = views;
	}

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

}

