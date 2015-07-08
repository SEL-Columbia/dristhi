package org.opensrp.domain;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

/**
 * @author muhammad.ahmed@ihsinformatics.com
 *  Created on May 25, 2015
 */
@TypeDiscriminator("doc.type == 'Error'")
public class ErrorTrace extends BaseDataObject {

	@JsonProperty
	private String id;
	@JsonProperty
	private Date date ;
	@JsonProperty
	private String name;
	@JsonProperty
	private String occurredAt;
	@JsonProperty
	private String stackTrace;
	@JsonProperty
	private String status;
	
	
	
	
	
	public ErrorTrace() {
		// TODO Auto-generated constructor stub
	}
	
	
	public ErrorTrace(String id,Date date, String name , String occuredAt, String stackTrace, String status) {
	this.date=date;
	this.id=id;
	this.name=name;
	this.occurredAt=occuredAt;
	this.stackTrace=stackTrace;
	this.status=status;
	
		
	}
	
	//getters and setters

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOccurredAt() {
		return occurredAt;
	}
	public void setOccurredAt(String occurredAt) {
		this.occurredAt = occurredAt;
	}
	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
