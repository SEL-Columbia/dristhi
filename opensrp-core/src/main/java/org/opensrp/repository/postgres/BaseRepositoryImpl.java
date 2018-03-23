package org.opensrp.repository.postgres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseRepositoryImpl<T> {
	
	public static int DEFAULT_FETCH_SIZE = 1000;
	
	public static String SERVER_VERSION = "server_version";
	
	protected static Logger logger = LoggerFactory.getLogger(BaseRepositoryImpl.class.toString());
	
	protected abstract Long retrievePrimaryKey(T t);
	
	protected abstract Object getUniqueField(T t);
	
}
