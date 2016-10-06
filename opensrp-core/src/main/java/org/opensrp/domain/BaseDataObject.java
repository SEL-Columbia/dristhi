package org.opensrp.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;

public abstract class BaseDataObject extends MotechBaseDataObject {

	@JsonProperty
	private User creator;
	@JsonProperty
	private DateTime dateCreated;
	@JsonProperty
	private User editor;
	@JsonProperty
	private DateTime dateEdited;
	@JsonProperty
	private Boolean voided;
	@JsonProperty
	private DateTime dateVoided;
	@JsonProperty
	private User voider;
	@JsonProperty
	private String voidReason;
	@JsonProperty
	private long serverVersion=System.currentTimeMillis();
	

	public BaseDataObject() {}
	
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public User getEditor() {
		return editor;
	}

	public void setEditor(User editor) {
		this.editor = editor;
	}

	public DateTime getDateEdited() {
		return dateEdited;
	}

	public void setDateEdited(DateTime dateEdited) {
		this.dateEdited = dateEdited;
	}

	public Boolean getVoided() {
		return voided;
	}

	public void setVoided(Boolean voided) {
		this.voided = voided;
	}

	public DateTime getDateVoided() {
		return dateVoided;
	}

	public void setDateVoided(DateTime dateVoided) {
		this.dateVoided = dateVoided;
	}

	public User getVoider() {
		return voider;
	}

	public void setVoider(User voider) {
		this.voider = voider;
	}

	public String getVoidReason() {
		return voidReason;
	}

	public void setVoidReason(String voidReason) {
		this.voidReason = voidReason;
	}
	
	public long getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(long version) {
		this.serverVersion = version;
	}
	
	public BaseDataObject withCreator(User creator) {
		this.creator = creator;
		return this;
	}

	public BaseDataObject withDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public BaseDataObject withEditor(User editor) {
		this.editor = editor;
		return this;
	}

	public BaseDataObject withDateEdited(DateTime dateEdited) {
		this.dateEdited = dateEdited;
		return this;
	}

	public BaseDataObject withVoided(Boolean voided) {
		this.voided = voided;
		return this;
	}

	public BaseDataObject withDateVoided(DateTime dateVoided) {
		this.dateVoided = dateVoided;
		return this;
	}

	public BaseDataObject withVoider(User voider) {
		this.voider = voider;
		return this;
	}

	public BaseDataObject withVoidReason(String voidReason) {
		this.voidReason = voidReason;
		return this;
	}
	
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
