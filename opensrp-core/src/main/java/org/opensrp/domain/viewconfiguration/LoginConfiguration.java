package org.opensrp.domain.viewconfiguration;

import org.codehaus.jackson.annotate.JsonProperty;

public class LoginConfiguration extends BaseConfiguration {

	@JsonProperty
	private boolean showPasswordCheckbox;

	@JsonProperty
	private String logoUrl;

	@JsonProperty
	private Background background;

	public boolean getShowPasswordCheckbox() {
		return showPasswordCheckbox;
	}

	public void setShowPasswordCheckbox(boolean showPasswordCheckbox) {
		this.showPasswordCheckbox = showPasswordCheckbox;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public Background getBackground() {
		return background;
	}

	public void setBackground(Background background) {
		this.background = background;
	}

	static class Background {

		@JsonProperty
		private String orientation;

		@JsonProperty
		private String startColor;

		@JsonProperty
		private String endColor;

		public String getOrientation() {
			return orientation;
		}

		public void setOrientation(String orientation) {
			this.orientation = orientation;
		}

		public String getStartColor() {
			return startColor;
		}

		public void setStartColor(String startColor) {
			this.startColor = startColor;
		}

		public String getEndColor() {
			return endColor;
		}

		public void setEndColor(String endColor) {
			this.endColor = endColor;
		}

	}
}
