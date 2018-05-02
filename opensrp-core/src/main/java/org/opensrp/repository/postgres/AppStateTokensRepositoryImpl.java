package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.AppStateToken;
import org.opensrp.domain.postgres.AppStateTokenExample;
import org.opensrp.repository.AppStateTokensRepository;
import org.opensrp.repository.postgres.mapper.AppStateTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("appStateTokensRepositoryPostgres")
public class AppStateTokensRepositoryImpl implements AppStateTokensRepository {
	
	@Autowired
	private AppStateTokenMapper mapper;
	
	@Override
	public AppStateToken get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		org.opensrp.domain.postgres.AppStateToken token = mapper.selectByPrimaryKey(Long.valueOf(id));
		return getDomainEntity(token);
	}
	
	@Override
	public List<AppStateToken> getAll() {
		List<org.opensrp.domain.postgres.AppStateToken> tokens = mapper.selectByExample(new AppStateTokenExample());
		List<AppStateToken> appStateTokens = new ArrayList<AppStateToken>();
		for (org.opensrp.domain.postgres.AppStateToken token : tokens) {
			appStateTokens.add(getDomainEntity(token));
		}
		return appStateTokens;
	}
	
	@Override
	public void safeRemove(AppStateToken entity) {
		AppStateTokenExample example = new AppStateTokenExample();
		example.createCriteria().andNameEqualTo(entity.getName());
		mapper.deleteByExample(example);
	}
	
	@Override
	public List<AppStateToken> findByName(String name) {
		AppStateTokenExample example = new AppStateTokenExample();
		example.createCriteria().andNameEqualTo(name);
		List<org.opensrp.domain.postgres.AppStateToken> tokens = mapper.selectByExample(example);
		List<AppStateToken> appStateTokens = new ArrayList<AppStateToken>();
		for (org.opensrp.domain.postgres.AppStateToken token : tokens) {
			appStateTokens.add(getDomainEntity(token));
		}
		return appStateTokens;
	}
	
	@Override
	public void update(AppStateToken entity) {
		AppStateTokenExample example = new AppStateTokenExample();
		example.createCriteria().andNameEqualTo(entity.getName());
		List<org.opensrp.domain.postgres.AppStateToken> tokens = mapper.selectByExample(example);
		if (tokens != null && !tokens.isEmpty()) {
			org.opensrp.domain.postgres.AppStateToken token = tokens.get(0);
			mapper.updateByPrimaryKey(getPostgresEntity(token, entity));
		}
	}
	
	@Override
	public void add(AppStateToken entity) {
		mapper.insertSelective(getPostgresEntity(entity));
	}
	
	private AppStateToken getDomainEntity(org.opensrp.domain.postgres.AppStateToken token) {
		return token == null ? null
		        : new AppStateToken(token.getName(), token.getValue(), token.getLastEditedDate(), token.getDescription());
	}
	
	private org.opensrp.domain.postgres.AppStateToken getPostgresEntity(AppStateToken entity) {
		return getPostgresEntity(new org.opensrp.domain.postgres.AppStateToken(), entity);
	}
	
	private org.opensrp.domain.postgres.AppStateToken getPostgresEntity(org.opensrp.domain.postgres.AppStateToken token,
	                                                                    AppStateToken entity) {
		if (entity == null)
			return null;
		token.setDescription(entity.getDescription());
		token.setName(entity.getName());
		token.setLastEditedDate(entity.getLastEditDate());
		token.setValue(entity.getValue().toString());
		return token;
	}
	
}
