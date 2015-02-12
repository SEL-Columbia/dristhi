package org.ei.drishti.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'Person'")
public class Person extends MotechBaseDataObject {
	@JsonProperty
	private String caseId;
	@JsonProperty
	private String anm;
	@JsonProperty
	private String village;
	@JsonProperty
	private String subCenter;
	@JsonProperty
	private String phc;
	@JsonProperty
	private String tbId;
	@JsonProperty
	private String name;
	@JsonProperty
	private String dob;
	@JsonProperty
	private String sex;
	@JsonProperty
	private String address;
	@JsonProperty
	private String contactNumber;
	@JsonProperty
	private Integer height;
	@JsonProperty
	private String patientType;
	
	@JsonProperty
	private String anmIdentifier;
	
	public String getAnmIdentifier() {
		return anmIdentifier;
	}

	public void setAnmIdentifier(String anmIdentifier) {
		this.anmIdentifier = anmIdentifier;
	}

	private Person()
	{
		
	}

	public Person(String caseId) {
		this.caseId = caseId;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getAnm() {
		return anm;
	}

	public void setAnm(String anm) {
		this.anm = anm;
	}

	public String getTbId() {
		return tbId;
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	public String getSubCenter() {
		return subCenter;
	}

	public void setSubCenter(String subCenter) {
		this.subCenter = subCenter;
	}

	public String getPhc() {
		return phc;
	}

	public void setPhc(String phc) {
		this.phc = phc;
	}

	public void setTbId(String tbId) {
		this.tbId = tbId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getPatientType() {
		return patientType;
	}

	public void setPatientType(String patientType) {
		this.patientType = patientType;
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

	public Person withName(String name) {
		setName(name);
		return this;
	}

	public Person withDob(String dob) {
		setDob(dob);
		return this;
	}

	public Person withSex(String sex) {
		setSex(sex);
		return this;
	}

	public Person withAddress(String address) {
		setAddress(address);
		return this;
	}

	public Person withContactNumber(String contactNumber) {
		setContactNumber(contactNumber);
		return this;
	}

	public Person withHeight(Integer height) {
		setHeight(height);
		return this;
	}

	public Person withAnm(String anm) {
		setAnm(anm);
		return this;
	}

	public Person withLocation(String village, String subCenter, String phc) {
        this.village = village;
        this.subCenter = subCenter;
        this.phc = phc;
        return this;
    }
	
	public Person withANMIdentifier(String anmIdentifier) {
        this.anmIdentifier = anmIdentifier;
        return this;
    }
}
