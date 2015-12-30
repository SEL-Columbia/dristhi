package org.opensrp.api.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

/**
 * An {@link Event} is a logical happening or experience of {@link BaseEntity} with the system as defined by the 
 * program workflow. The difference of an {@link Event} and other activities in system is that the data of {@link Event} 
 * is meaningful for business clients of system and also the data links back with international standard dictionaries. 
 * An {@link Event} in system is recorded via a form filled by data {@link Provider}. The form data is represented by 
 * {@link FormSubmission}
 */
public class Event extends BaseDataObject{

	private String eventId;
	private String baseEntityId;
	private String locationId;
	private DateTime eventDate;
	private String eventType;
	private String formSubmissionId;
	private String providerId;
	private List<Obs> obs;
	private String entityType;
	private Map<String, String> details;
	private long version;
	
	public Event() {
		this.version = System.currentTimeMillis();
	}

	public Event(String baseEntityId, String eventId, String eventType, DateTime eventDate, String entityType, 
			String providerId, String locationId, String formSubmissionId) {
		this.baseEntityId = baseEntityId;
		this.eventId = eventId;
		this.eventType = eventType;
		this.eventDate = eventDate;
		this.entityType = entityType;
		this.providerId = providerId;
		this.locationId = locationId;
		this.formSubmissionId = formSubmissionId;
		this.version = System.currentTimeMillis();
	}
	
	public List<Obs> getObs() {
		return obs;
	}

	/**
	 * WARNING: Overrides all existing obs
	 * @param obs
	 * @return
	 */
	public void setObs(List<Obs> obs) {
		this.obs = obs;
	}
	
	public void addObs(Obs observation) {
		if(obs == null){
			obs = new ArrayList<>();
		}
		
		obs.add(observation);
	}
	
	public String getBaseEntityId() {
		return baseEntityId;
	}
	
	public void setBaseEntityId(String baseEntityId) {
		this.baseEntityId = baseEntityId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public DateTime getEventDate() {
		return eventDate;
	}

	public void setEventDate(DateTime eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getFormSubmissionId() {
		return formSubmissionId;
	}

	public void setFormSubmissionId(String formSubmissionId) {
		this.formSubmissionId = formSubmissionId;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Map<String, String> getDetails() {
		return details;
	}

	public void setDetails(Map<String, String> details) {
		this.details = details;
	}
	
	public void addDetails(String key, String val) {
		if(details == null){
			details = new HashMap<>();
		}
		details.put(key, val);
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Event withBaseEntityId(String baseEntityId) {
		this.baseEntityId = baseEntityId;
		return this;
	}

	public Event withLocationId(String locationId) {
		this.locationId = locationId;
		return this;
	}

	public Event withEventDate(DateTime eventDate) {
		this.eventDate = eventDate;
		return this;
	}

	public Event withEventType(String eventType) {
		this.eventType = eventType;
		return this;
	}

	public Event withFormSubmissionId(String formSubmissionId) {
		this.formSubmissionId = formSubmissionId;
		return this;
	}

	public Event withProviderId(String providerId) {
		this.providerId = providerId;
		return this;
	}

	public Event withEntityType(String entityType) {
		this.entityType = entityType;
		return this;
	}
	
	/**
	 * WARNING: Overrides all existing obs
	 * @param obs
	 * @return
	 */
	public Event withObs(List<Obs> obs) {
		this.obs = obs;
		return this;
	}
	
	public Event withObs(Obs observation) {
		if(obs == null){
			obs = new ArrayList<>();
		}
		obs.add(observation);
		return this;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
