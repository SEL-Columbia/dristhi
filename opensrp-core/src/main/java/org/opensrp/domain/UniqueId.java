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
	
	@Id
	@GeneratedValue
	@Column(name = "_id")
	private Long id;
	
	@Column(name = "openmrs_id")
	private String openmrsId;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "used_by")
	private String usedBy;
	
	@Column(name = "location")
	private String location;
	
	@Column(name = "created_at",columnDefinition="DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@Column(name = "updated_at",columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
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
