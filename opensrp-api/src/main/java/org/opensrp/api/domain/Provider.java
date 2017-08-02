package org.opensrp.api.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The person who provided data for {@link Event}
 */
public class Provider extends BaseEntity {

	private String fullName;

	protected Provider() {
		
	}
	
	public Provider(String baseEntityId) {
		super(baseEntityId);
	}

	public Provider(String baseEntityId, String fullName) {
		super(baseEntityId);
		this.setFullName(fullName);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
