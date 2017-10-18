package org.opensrp.domain;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author SGithengi
 *
 */
public class View {
	@JsonProperty
	private String identifier;
	@JsonProperty
	private String parent;
	@JsonProperty
	private String type;
	@JsonProperty
	private String orientation;
	@JsonProperty
	private int position;
	@JsonProperty
	private String layoutWeight;
	@JsonProperty
	private boolean visible;
	@JsonProperty
	private String label;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getLayoutWeight() {
		return layoutWeight;
	}

	public void setLayoutWeight(String layoutWeight) {
		this.layoutWeight = layoutWeight;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
