package org.opensrp.search;

import java.util.Map;

import org.joda.time.DateTime;

public class ClientSearchBean {
	
	private String nameLike;
	
	private String gender;
	
	private DateTime birthdateFrom;
	
	private DateTime birthdateTo;
	
	private DateTime deathdateFrom;
	
	private DateTime deathdateTo;
	
	private String attributeType;
	
	private String attributeValue;
	
	private DateTime lastEditFrom;
	
	private DateTime lastEditTo;
	
	private Map<String, String> identifiers;
	
	private Map<String, String> attributes;
	
	public String getNameLike() {
		return nameLike;
	}
	
	public void setNameLike(String nameLike) {
		this.nameLike = nameLike;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public DateTime getBirthdateFrom() {
		return birthdateFrom;
	}
	
	public void setBirthdateFrom(DateTime birthdateFrom) {
		this.birthdateFrom = birthdateFrom;
	}
	
	public DateTime getBirthdateTo() {
		return birthdateTo;
	}
	
	public void setBirthdateTo(DateTime birthdateTo) {
		this.birthdateTo = birthdateTo;
	}
	
	public DateTime getDeathdateFrom() {
		return deathdateFrom;
	}
	
	public void setDeathdateFrom(DateTime deathdateFrom) {
		this.deathdateFrom = deathdateFrom;
	}
	
	public DateTime getDeathdateTo() {
		return deathdateTo;
	}
	
	public void setDeathdateTo(DateTime deathdateTo) {
		this.deathdateTo = deathdateTo;
	}
	
	public String getAttributeType() {
		return attributeType;
	}
	
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}
	
	public String getAttributeValue() {
		return attributeValue;
	}
	
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	
	public DateTime getLastEditFrom() {
		return lastEditFrom;
	}
	
	public void setLastEditFrom(DateTime lastEditFrom) {
		this.lastEditFrom = lastEditFrom;
	}
	
	public DateTime getLastEditTo() {
		return lastEditTo;
	}
	
	public void setLastEditTo(DateTime lastEditTo) {
		this.lastEditTo = lastEditTo;
	}
	
	public Map<String, String> getIdentifiers() {
		return identifiers;
	}
	
	public void setIdentifiers(Map<String, String> identifiers) {
		this.identifiers = identifiers;
	}
	
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
}
