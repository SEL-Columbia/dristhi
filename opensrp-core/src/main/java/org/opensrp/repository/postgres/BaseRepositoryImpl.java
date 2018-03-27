package org.opensrp.repository.postgres;

import org.opensrp.common.AllConstants.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseRepositoryImpl<T> {
	
	public static int DEFAULT_FETCH_SIZE = 1000;
	
	public static String SERVER_VERSION = "server_version";
	
	protected static Logger logger = LoggerFactory.getLogger(BaseRepositoryImpl.class.toString());
	
	protected abstract Long retrievePrimaryKey(T t);
	
	protected abstract Object getUniqueField(T t);
	
	protected String getOrderByClause(String sortBy, String sortOrder) {
		String orderByClause = sortBy == null || sortBy == BaseEntity.SERVER_VERSIOIN ? SERVER_VERSION : sortBy;
		orderByClause += " " + ((sortOrder == null || !sortOrder.toLowerCase().matches("(asc)|(desc)")) ? "asc" : sortOrder);
		return orderByClause;
	}
	
}
