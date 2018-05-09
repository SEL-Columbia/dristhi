package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.AppStateToken;

public interface AppStateTokensRepository extends BaseRepository<AppStateToken> {
	
	List<AppStateToken> findByName(String name);
	
	void update(AppStateToken entity);
	
	void add(AppStateToken entity);
	
}
