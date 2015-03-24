package org.opensrp.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.opensrp.api.constants.Gender;


@TypeDiscriminator("doc.type == 'Provider'")
public class Provider extends BaseDataObject {
	@JsonProperty
	private Map<String, String> identifiers;
	@JsonProperty
	private String baseEntityId;
	@JsonProperty
	private BaseEntity baseEntity;

	public Provider() {
	}

	public Provider(String baseEntityId, String firstName, String middleName,
			String lastName, Date birthdate, Date deathdate,
			Boolean birthdateApprox, Boolean deathdateApprox, String gender) {
		this.baseEntity = new BaseEntity(baseEntityId, firstName, middleName,
				lastName, birthdate, deathdate, birthdateApprox,
				deathdateApprox, gender, null, null);
		this.baseEntityId = baseEntityId;
	}

	public Provider(String baseEntityId, String firstName, String middleName,
			String lastName, Date birthdate, Date deathdate,
			Boolean birthdateApprox, Boolean deathdateApprox, String gender,
			String identifierType, String identifier) {
		this.baseEntity = new BaseEntity(baseEntityId, firstName, middleName,
				lastName, birthdate, deathdate, birthdateApprox,
				deathdateApprox, gender, null, null);
		this.baseEntityId = baseEntityId;
		this.identifiers = new HashMap<>();
		identifiers.put(identifierType, identifier);
	}

	public Provider(String baseEntityId, String firstName, String middleName,
			String lastName, Date birthdate, Date deathdate,
			Boolean birthdateApprox, Boolean deathdateApprox, String gender,
			List<Address> addresses, Map<String, String> identifiers,
			Map<String, Object> attributes) {
		this.baseEntity = new BaseEntity(baseEntityId, firstName, middleName,
				lastName, birthdate, deathdate, birthdateApprox,
				deathdateApprox, gender, addresses, attributes);
		this.baseEntityId = baseEntityId;
		this.identifiers = identifiers;
	}

	public Provider(String baseEntityId, String firstName, String middleName,
			String lastName, Date birthdate, Date deathdate,
			Boolean birthdateApprox, Boolean deathdateApprox, Gender gender,
			List<Address> addresses, Map<String, String> identifiers,
			Map<String, Object> attributes) {
		this.baseEntity = new BaseEntity(baseEntityId, firstName, middleName,
				lastName, birthdate, deathdate, birthdateApprox,
				deathdateApprox, gender, addresses, attributes);
		this.baseEntityId = baseEntityId;
		this.identifiers = identifiers;
	}

	public Map<String, String> getIdentifiers() {
		return identifiers;
	}

	/**
	 * WARNING: Overrides all existing identifiers
	 * 
	 * @param identifiers
	 * @return
	 */
	public void setIdentifiers(Map<String, String> identifiers) {
		this.identifiers = identifiers;
	}

	public void addIdentifier(String identifierType, String identifier) {
		if (identifiers == null) {
			identifiers = new HashMap<>();
		}

		identifiers.put(identifierType, identifier);
	}

	public void removeIdentifier(String identifierType) {
		identifiers.remove(identifierType);
	}

	public String getBaseEntityId() {
		return baseEntityId;
	}

	public void setBaseEntityId(String baseEntityId) {
		this.baseEntityId = baseEntityId;
	}

	public BaseEntity getBaseEntity() {
		return baseEntity;
	}

	public void setBaseEntity(BaseEntity baseEntity) {
		this.baseEntity = baseEntity;
		this.baseEntityId = baseEntity.getId();
	}

	public Provider withBaseEntity(BaseEntity baseEntity) {
		this.baseEntity = baseEntity;
		this.baseEntityId = baseEntity.getId();
		return this;
	}

	public Provider withBaseEntityId(String baseEntityId) {
		this.baseEntityId = baseEntityId;
		return this;
	}

	/**
	 * WARNING: Overrides all existing identifiers
	 * 
	 * @param identifiers
	 * @return
	 */
	public Provider withIdentifiers(Map<String, String> identifiers) {
		this.identifiers = identifiers;
		return this;
	}

	public Provider withIdentifier(String identifierType, String identifier) {
		if (identifiers == null) {
			identifiers = new HashMap<>();
		}
		identifiers.put(identifierType, identifier);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o, "id", "revision");
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, "id", "revision");
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
