package org.opensrp.domain.viewconfiguration;

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
	private String type;
	@JsonProperty
	private String orientation;
	@JsonProperty
	private String label;
	@JsonProperty
	private boolean visible;
	@JsonProperty
	private Residence residence;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Residence getResidence() {
		return residence;
	}

	public void setResidence(Residence residence) {
		this.residence = residence;
	}

	static class Residence {
		@JsonProperty
		private String parent;
		@JsonProperty
		private int position;
		@JsonProperty(value = "layout_weight")
		private String layoutWeight;

		public String getParent() {
			return parent;
		}

		public void setParent(String parent) {
			this.parent = parent;
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

	}

}
