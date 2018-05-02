package org.opensrp.repository;

import java.util.List;

public interface BaseRepository<T> {
	
	T get(String id);
	
	void add(T entity);
	
	void update(T entity);
	
	List<T> getAll();
	
	void safeRemove(T entity);
	
}
