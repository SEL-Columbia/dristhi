package org.opensrp.search;

public class StockSearchBean {
	
	private String identifier;
	
	private String stockTypeId;
	
	private String transactionType;
	
	private String providerId;
	
	private String value;
	
	private String dateCreated;
	
	private String toFrom;
	
	private String dateUpdated;
	
	private Long serverVersion;
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getStockTypeId() {
		return stockTypeId;
	}
	
	public void setStockTypeId(String stockTypeId) {
		this.stockTypeId = stockTypeId;
	}
	
	public String getTransactionType() {
		return transactionType;
	}
	
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	public String getProviderId() {
		return providerId;
	}
	
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public String getToFrom() {
		return toFrom;
	}
	
	public void setToFrom(String toFrom) {
		this.toFrom = toFrom;
	}
	
	public String getDateUpdated() {
		return dateUpdated;
	}
	
	public void setDateUpdated(String dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	
	public Long getServerVersion() {
		return serverVersion;
	}
	
	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}
	
}
