package org.opensrp.dto.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;

public class MultimediaDTO {

	@JsonProperty
	private String caseId;
	@JsonProperty
	private String providerId;
	@JsonProperty
	private String contentType;
	@JsonProperty
	private String filePath;
	@JsonProperty
	private String fileCategory;
	
	public MultimediaDTO()
	{
		
	}
	public MultimediaDTO(String caseId, String providerId, String contentType, String filePath,String fileCategory) {
		this.caseId = caseId;
		this.providerId = providerId;
		this.contentType = contentType;
		this.filePath = filePath;
		this.fileCategory = fileCategory;
	}

	public String caseId() {
		return this.caseId;
	}

	public String providerId() {
		return this.providerId;
	}

	public String contentType() {
		return this.contentType;
	}

	public String filePath() {
		return this.filePath;
	}

	public String fileCategory() {
		return this.fileCategory;
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
