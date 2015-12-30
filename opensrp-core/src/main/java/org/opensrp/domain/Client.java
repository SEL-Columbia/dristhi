package org.opensrp.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.opensrp.common.Gender;

@TypeDiscriminator("doc.type == 'Client'")
public class Client extends BaseEntity {
	@JsonProperty
	private String firstName;
	@JsonProperty
	private String middleName;
	@JsonProperty
	private String lastName;
	@JsonProperty
	private Date birthdate;
	@JsonProperty
	private Date deathdate;
	@JsonProperty
	private Boolean birthdateApprox;
	@JsonProperty
	private Boolean deathdateApprox;
	@JsonProperty
	private String gender;

	protected Client() {
		
	}

	public Client(String baseEntityId) {
		super(baseEntityId);
	}
	
	public Client(String baseEntityId, String firstName, String middleName, String lastName, Date birthdate, 
			Date deathdate, Boolean birthdateApprox, Boolean deathdateApprox, String gender) {
		super(baseEntityId);
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.birthdate = birthdate;
		this.deathdate = deathdate;
		this.birthdateApprox = birthdateApprox;
		this.deathdateApprox = deathdateApprox;
		this.gender = gender;
	}
	
	public Client(String baseEntityId, String firstName, String middleName, String lastName, Date birthdate, 
			Date deathdate, Boolean birthdateApprox, Boolean deathdateApprox, String gender, 
			String identifierType, String identifier) {
		super(baseEntityId);
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.birthdate = birthdate;
		this.deathdate = deathdate;
		this.birthdateApprox = birthdateApprox;
		this.deathdateApprox = deathdateApprox;
		this.gender = gender;
		addIdentifier(identifierType, identifier);
	}
	
	public Client(String baseEntityId, String firstName, String middleName, String lastName, Date birthdate, Date deathdate, 
			Boolean birthdateApprox, Boolean deathdateApprox, String gender, List<Address> addresses,
			Map<String, String> identifiers, Map<String, Object> attributes) {
		super(baseEntityId);
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.birthdate = birthdate;
		this.deathdate = deathdate;
		this.birthdateApprox = birthdateApprox;
		this.deathdateApprox = deathdateApprox;
		this.gender = gender;
		setIdentifiers(identifiers);
		setAddresses(addresses);
		setAttributes(attributes);
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public Date getDeathdate() {
		return deathdate;
	}

	public void setDeathdate(Date deathdate) {
		this.deathdate = deathdate;
	}

	public Boolean getBirthdateApprox() {
		return birthdateApprox;
	}

	public void setBirthdateApprox(Boolean birthdateApprox) {
		this.birthdateApprox = birthdateApprox;
	}

	public Boolean getDeathdateApprox() {
		return deathdateApprox;
	}

	public void setDeathdateApprox(Boolean deathdateApprox) {
		this.deathdateApprox = deathdateApprox;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Client withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public Client withMiddleName(String middleName) {
		this.middleName = middleName;
		return this;
	}

	public Client withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public Client withName(String firstName, String middleName,
			String lastName) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		return this;
	}

	public Client withBirthdate(Date birthdate, Boolean isApproximate) {
		this.birthdate = birthdate;
		this.birthdateApprox = isApproximate;
		return this;
	}

	public Client withDeathdate(Date deathdate, Boolean isApproximate) {
		this.deathdate = deathdate;
		this.deathdateApprox = isApproximate;
		return this;
	}

	public Client withGender(String gender) {
		this.gender = gender;
		return this;
	}

	public Client withGender(Gender gender) {
		this.gender = gender.name();
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
