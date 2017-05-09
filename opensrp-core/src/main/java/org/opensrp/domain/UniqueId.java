package org.opensrp.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "unique_ids")
public class UniqueId {
	
	public static final String tbName = "unique_ids";
	
	public static final String COL_OPENMRSID = "openmrs_id";
	
	public static final String COL_STATUS = "status";
	
	public static final String COL_USEDBY = "used_by";
	
	public static final String COL_LOCATION = "location";
	
	public static final String COL_CREATED_AT = "created_at";
	
	public static final String COL_UPDATED_AT = "updated_at";
	
	public static String STATUS_USED = "used";
	
	public static String STATUS_NOT_USED = "not_used";
	
	@Id
	@GeneratedValue
	@Column(name = "_id")
	private Long id;
	
	@Column(name = COL_OPENMRSID)
	private String openmrsId;
	
	@Column(name = COL_STATUS)
	private String status;
	
	@Column(name = COL_USEDBY)
	private String usedBy;
	
	@Column(name = COL_LOCATION)
	private String location;
	
	@Column(name = COL_CREATED_AT, columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@Column(name = COL_UPDATED_AT, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getOpenmrsId() {
		return openmrsId;
	}
	
	public void setOpenmrsId(String openmrsId) {
		this.openmrsId = openmrsId;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getUsedBy() {
		return usedBy;
	}
	
	public void setUsedBy(String usedBy) {
		this.usedBy = usedBy;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public Date getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	
}
