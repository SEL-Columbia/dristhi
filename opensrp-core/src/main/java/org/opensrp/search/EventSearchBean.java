
package org.opensrp.search;

import org.joda.time.DateTime;

public class EventSearchBean {
	
	private String baseEntityId;
	
	private DateTime eventDateFrom;
	
	private DateTime eventDateTo;
	
	private String eventType;
	
	private String entityType;
	
	private String providerId;
	
	private String locationId;
	
	private DateTime lastEditFrom;
	
	private DateTime lastEditTo;
	
	private String team;
	
	private String teamId;
	
	private Long serverVersion;
	
	public String getBaseEntityId() {
		return baseEntityId;
	}
	
	public void setBaseEntityId(String baseEntityId) {
		this.baseEntityId = baseEntityId;
	}
	
	public DateTime getEventDateFrom() {
		return eventDateFrom;
	}
	
	public void setEventDateFrom(DateTime eventDateFrom) {
		this.eventDateFrom = eventDateFrom;
	}
	
	public DateTime getEventDateTo() {
		return eventDateTo;
	}
	
	public void setEventDateTo(DateTime eventDateTo) {
		this.eventDateTo = eventDateTo;
	}
	
	public String getEventType() {
		return eventType;
	}
	
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public String getEntityType() {
		return entityType;
	}
	
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	
	public String getProviderId() {
		return providerId;
	}
	
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	public String getLocationId() {
		return locationId;
	}
	
	public void setLocationId(String locationId) {
		this.locationId = locationId;
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
	
	public String getTeam() {
		return team;
	}
	
	public void setTeam(String team) {
		this.team = team;
	}
	
	public String getTeamId() {
		return teamId;
	}
	
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	
	public Long getServerVersion() {
		return serverVersion;
	}
	
	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}
	
}
