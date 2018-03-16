package org.opensrp.repository.postgres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseRepositoryImpl<T> {
	
	protected static Logger logger = LoggerFactory.getLogger(BaseRepositoryImpl.class.toString());
	
	protected abstract Long retrievePrimaryKey(T t);
	
	protected abstract Object getUniqueField(T t);
	
}
