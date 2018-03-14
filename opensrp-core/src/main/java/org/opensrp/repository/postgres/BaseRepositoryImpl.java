package org.opensrp.repository.postgres;

public abstract class BaseRepositoryImpl<T> {
	
	protected abstract Long retrievePrimaryKey(T t);
	
	protected abstract Object getUniqueField(T t);
	
}
