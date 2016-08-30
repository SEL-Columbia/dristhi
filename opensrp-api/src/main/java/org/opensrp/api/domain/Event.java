package org.opensrp.api.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

/**
 * An {@link Event} is a logical happening or experience of {@link BaseEntity} with the system as defined by the 
 * program workflow. The difference of an {@link Event} and other activities in system is that the data of {@link Event} 
 * is meaningful for business clients of system and also the data links back with international standard dictionaries. 
 * An {@link Event} in system is recorded via a form filled by data {@link Provider}. The form data is represented by 
 * {@link FormSubmission}
 */
public class Event extends BaseDataObject{
	private Map<String, String> identifiers;
	private String baseEntityId;
	private String locationId;
	private DateTime eventDate;
	private String eventType;
	private String formSubmissionId;
	private String providerId;
	private String status;
	private Map<String, DateTime> statusHistory;
	private String priority;
	private List<String>  episodeOfCare;
	private List<String> referrals;
	private String category;
	private int duration;
	private String reason;
	private List<Obs> obs;
	private String entityType;
	private Map<String, String> details;
	private long version;
	private List<Photo> photos;
	
	public Event() {
		this.version = System.currentTimeMillis();
	}

	public Event(String baseEntityId, String eventType, DateTime eventDate, String entityType, 
			String providerId, String locationId, String formSubmissionId) {
		this.baseEntityId = baseEntityId;
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

	public Map<String, String> getIdentifiers() {
		if(identifiers == null){
			identifiers = new HashMap<>();
		}
		return identifiers;
	}

	public String getIdentifier(String identifierType) {
		if(identifiers == null){
			return null;
		}
		for (String k : identifiers.keySet()) {
			if(k.equalsIgnoreCase(identifierType)){
				return identifiers.get(k);
			}
		}
		return null;
	}
	
	/**
	 * Returns field matching the regex. Note that incase of multiple fields matching criteria 
	 * function would return first match. The must be well formed to find out a single value
	 * @param regex
	 * @return
	 */
	public String getIdentifierMatchingRegex(String regex) {
		for (Entry<String, String> a : getIdentifiers().entrySet()) {
			if(a.getKey().matches(regex)){
				return a.getValue();
			}
		}
		return null;
	}
	
	public void setIdentifiers(Map<String, String> identifiers) {
		this.identifiers = identifiers;
	}

	public void addIdentifier(String identifierType, String identifier) {
		if(identifiers == null){
			identifiers = new HashMap<>();
		}
		
		identifiers.put(identifierType, identifier);
	}

	public void removeIdentifier(String identifierType) {
		identifiers.remove(identifierType);
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

	/**
	 * WARNING: Overrides all existing identifiers
	 * @param identifiers
	 * @return
	 */
	public Event withIdentifiers(Map<String, String> identifiers) {
		this.identifiers = identifiers;
		return this;
	}
	
	public Event withIdentifier(String identifierType, String identifier) {
		if(identifiers == null){
			identifiers = new HashMap<>();
		}
		identifiers.put(identifierType, identifier);
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
