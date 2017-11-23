package org.opensrp.domain.viewconfiguration;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.opensrp.domain.BaseDataObject;

/**
 * @author SGithengi
 */
@TypeDiscriminator("doc.type == 'ViewConfiguration'")
public class ViewConfiguration extends BaseDataObject {
	
	private static final long serialVersionUID = 1890883609898207737L;
	
	@JsonProperty
	private String type;
	
	@JsonProperty
	private String identifier;
	
	@JsonProperty
	private BaseConfiguration metadata;
	
	@JsonProperty
	private List<View> views;
	
	@JsonProperty
	private Map<String, String> labels;
	
	@JsonProperty
	private Object jsonView;
	
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
	
	public BaseConfiguration getMetadata() {
		return metadata;
	}
	
	public void setMetadata(BaseConfiguration metadata) {
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
	
	public Object getJsonView() {
		return jsonView;
	}

	public void setJsonView(Object jsonView) {
		this.jsonView = jsonView;
	}
	
}
