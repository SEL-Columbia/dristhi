package org.opensrp.domain;

import org.joda.time.DateTime;

public class ContactPoint {

	private String type;
	private String use;
	private String number;
	private int preference;
	private DateTime startDate;
	private DateTime endDate;
	
	public ContactPoint() {
		
	}
	

	public ContactPoint(String type, String use, String number, int preference, DateTime startDate, DateTime endDate) {
		this.type = type;
		this.use = use;
		this.number = number;
		this.preference = preference;
		this.startDate = startDate;
		this.endDate = endDate;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getPreference() {
		return preference;
	}

	public void setPreference(int preference) {
		this.preference = preference;
	}

	public DateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public DateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}
	
	
}
