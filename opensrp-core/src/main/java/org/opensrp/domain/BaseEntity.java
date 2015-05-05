package org.opensrp.domain;

import java.util.ArrayList;
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

@TypeDiscriminator("doc.type == 'BaseEntity'")
public class BaseEntity extends BaseDataObject {

	@JsonProperty
	private String baseEntityId;
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
	@JsonProperty
	private List<Address> addresses;
	@JsonProperty
	private Map<String, Object> attributes;

	public BaseEntity() {
	}

	public BaseEntity(String baseEntityId, String firstName, String middleName,
			String lastName, Date birthdate, Date deathdate,
			Boolean birthdateApprox, Boolean deathdateApprox, String gender,
			List<Address> addresses, Map<String, Object> attributes) {
		this.setId(baseEntityId);
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.birthdate = birthdate;
		this.deathdate = deathdate;
		this.birthdateApprox = birthdateApprox;
		this.deathdateApprox = deathdateApprox;
		this.gender = gender;
		this.addresses = addresses;
		this.attributes = attributes;
	}

	/**
	 * Allows to instantiate from a list of predefined genders in {@link Gender}
	 * 
	 * @param firstName
	 * @param lastName
	 * @param birthdate
	 * @param deathdate
	 * @param birthdateApprox
	 * @param deathdateApprox
	 * @param gender
	 * @param addresses
	 * @param attributes
	 */
	public BaseEntity(String baseEntityId, String firstName, String middleName,
			String lastName, Date birthdate, Date deathdate,
			Boolean birthdateApprox, Boolean deathdateApprox, Gender gender,
			List<Address> addresses, Map<String, Object> attributes) {
		this.baseEntityId = baseEntityId;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.birthdate = birthdate;
		this.deathdate = deathdate;
		this.birthdateApprox = birthdateApprox;
		this.deathdateApprox = deathdateApprox;
		this.gender = gender.name();
		this.addresses = addresses;
		this.attributes = attributes;
	}

	public String getBaseEntityId() {
		return baseEntityId;
	}

	public void setBaseEntityId(String baseEntityId) {
		this.baseEntityId = baseEntityId;
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

	public List<Address> getAddresses() {
		return addresses;
	}

	/**
	 * WARNING: Overrides all existing addresses
	 * 
	 * @param addresses
	 * @return
	 */
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public void addAddress(Address address) {
		if (addresses == null) {
			addresses = new ArrayList<>();
		}
		addresses.add(address);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * WARNING: Overrides all existing attributes
	 * 
	 * @param attributes
	 * @return
	 */
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void addAttribute(String name, Object value) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}

		attributes.put(name, value);
	}

	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	public BaseEntity withBaseEntityId(String baseEntityId) {
		this.baseEntityId = baseEntityId;
		return this;
	}

	public BaseEntity withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public BaseEntity withMiddleName(String middleName) {
		this.middleName = middleName;
		return this;
	}

	public BaseEntity withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public BaseEntity withName(String firstName, String middleName,
			String lastName) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		return this;
	}

	public BaseEntity withBirthdate(Date birthdate, Boolean isApproximate) {
		this.birthdate = birthdate;
		this.birthdateApprox = isApproximate;
		return this;
	}

	public BaseEntity withDeathdate(Date deathdate, Boolean isApproximate) {
		this.deathdate = deathdate;
		this.deathdateApprox = isApproximate;
		return this;
	}

	public BaseEntity withGender(String gender) {
		this.gender = gender;
		return this;
	}

	public BaseEntity withGender(Gender gender) {
		this.gender = gender.name();
		return this;
	}

	/**
	 * WARNING: Overrides all existing addresses
	 * 
	 * @param addresses
	 * @return
	 */
	public BaseEntity withAddresses(List<Address> addresses) {
		this.addresses = addresses;
		return this;
	}

	public BaseEntity withAddress(Address address) {
		if (addresses == null) {
			addresses = new ArrayList<>();
		}
		addresses.add(address);
		return this;
	}

	/**
	 * WARNING: Overrides all existing attributes
	 * 
	 * @param attributes
	 * @return
	 */
	public BaseEntity withAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
		return this;
	}

	public BaseEntity withAttribute(String name, Object value) {
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		attributes.put(name, value);
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
