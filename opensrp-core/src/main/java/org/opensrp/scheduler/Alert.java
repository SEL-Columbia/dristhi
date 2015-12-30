package org.opensrp.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.DateUtil;
import org.opensrp.dto.AlertStatus;

/**
 * The entity which helps in identifying the type of action applicable for the entity or provider
 */
@TypeDiscriminator("doc.type === 'Alert'")
public class Alert extends MotechBaseDataObject {
	public enum AlertType{
		notification, reminder
	}
	public enum TriggerType{
		schedule, report, event, caseClosed
	}
    @JsonProperty
    private String providerId;
    @JsonProperty
    private String entityId;
    @JsonProperty
    private String beneficiaryType;
    @JsonProperty
    private String alertType;
    @JsonProperty
    private String triggerType;
    @JsonProperty
    private String triggerName;
    @JsonProperty
    private String triggerCode;
    @JsonProperty
    private String startDate;
    @JsonProperty
    private String expiryDate;
    @JsonProperty
    private String alertStatus;
    @JsonProperty
    private String closingPeriod;
    @JsonProperty
    private String dateClosed;
    @JsonProperty
    private String dateComplete;
    @JsonProperty
    private String reasonClosed;
    @JsonProperty
    private Boolean isActive;
    @JsonProperty
    private long timestamp;
    @JsonProperty
    private Map<String, String> details;

	private Alert() {
    }
    
    public Alert(String providerId, String entityId, String beneficiaryType, AlertType alertType, 
    		TriggerType triggerType, String triggerName, String triggerCode, DateTime startDate, DateTime expiryDate,
			AlertStatus alertStatus, Map<String, String> details) {
		this.providerId = providerId;
		this.entityId = entityId;
		this.beneficiaryType = beneficiaryType;
		this.alertType = alertType.name();
		this.triggerType = triggerType.name();
		this.triggerName = triggerName;
		this.triggerCode = triggerCode;
		this.startDate = startDate.toLocalDate().toString();
		this.expiryDate = expiryDate.toLocalDate().toString();
		this.alertStatus = alertStatus.name();
		this.isActive = true;
		this.timestamp = DateUtil.now().getMillis();
		this.details = details;
	}

    public void markAlertAsClosed(String reasonForClose) {
    	if(alertStatus.equalsIgnoreCase(AlertStatus.closed.name())
    			|| alertStatus.equalsIgnoreCase(AlertStatus.complete.name())){
    		throw new IllegalStateException("Alert was found "+alertStatus);
    	}
    	this.closingPeriod = this.alertStatus;
    	this.reasonClosed = reasonForClose;
    	this.alertStatus = AlertStatus.closed.name();
    	this.dateClosed = new DateTime().toLocalDate().toString();
    	this.isActive = false;
    }
    
    public void markAlertAsComplete(String completionDate) {
    	if(alertStatus.equalsIgnoreCase(AlertStatus.closed.name())
    			|| alertStatus.equalsIgnoreCase(AlertStatus.complete.name())){
    		throw new IllegalStateException("Alert was found "+alertStatus);
    	}
    	this.dateComplete = completionDate;
    	this.closingPeriod = this.alertStatus;
    	this.alertStatus = AlertStatus.complete.name();
    	this.dateClosed = new DateTime().toLocalDate().toString();
    	this.isActive = false;
	}

	public String providerId() {
		return providerId;
	}

	public String entityId() {
		return entityId;
	}

	public String beneficiaryType() {
		return beneficiaryType;
	}

	public String alertType() {
		return alertType;
	}

	public String triggerType() {
		return triggerType;
	}

	public String triggerName() {
		return triggerName;
	}

	public String triggerCode() {
		return triggerCode;
	}

	public String startDate() {
		return startDate;
	}

	public String expiryDate() {
		return expiryDate;
	}

	public String alertStatus() {
		return alertStatus;
	}

	public String closingPeriod() {
		return closingPeriod;
	}

	public String dateClosed() {
		return dateClosed;
	}

	public String reasonClosed() {
		return reasonClosed;
	}

	@JsonIgnore
	public Boolean isActive() {
		return isActive;
	}

	public long timestamp() {
		return timestamp;
	}

	public Map<String, String> details() {
		return details;
	}
    
    public Alert withDetails(String key, String val) {
    	if(details == null){
    		details = new HashMap<>();
    	}
    	details.put(key, val);
		return this;
	}


    String getProviderId() {
		return providerId;
	}

	String getEntityId() {
		return entityId;
	}

	String getBeneficiaryType() {
		return beneficiaryType;
	}

	String getTriggerType() {
		return triggerType;
	}
	
	String getAlertType() {
		return alertType;
	}
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "timeStamp", "revision");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "timeStamp", "revision");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
