package org.opensrp.service;

import java.util.List;

import org.opensrp.domain.AppStateToken;
import org.opensrp.repository.AllAppStateTokens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.StringUtils;

@Service
public class ConfigService {
	
	private final AllAppStateTokens allAppStateTokens;
	
	@Autowired
	public ConfigService(AllAppStateTokens allAppStateTokens)
	{
		this.allAppStateTokens = allAppStateTokens;
	}
	
	/**
	 * @param tokenName
	 * @return AppStateToken with given name. Since model is supposed to keep track of system`s state at any given time it throws IllegalStateException incase multiple Tokens found with same name.
	 */
	public AppStateToken getAppStateTokenByName(Enum<?> tokenName) {
		List<AppStateToken> ol = allAppStateTokens.findByName(tokenName.name());
		if(ol.size() > 1){
			throw new IllegalStateException("System was found to have multiple token with same name ("+tokenName.name()+"). This can lead to potential critical inconsistencies.");
		}
		
		return ol.size()==0?null:ol.get(0);
	}
	
	public void updateAppStateToken(Enum<?> tokenName, Object value) {
		List<AppStateToken> ol = allAppStateTokens.findByName(tokenName.name());
		if(ol.size() > 1){
			throw new IllegalStateException("System was found to have multiple token with same name ("+tokenName.name()+"). This can lead to potential critical inconsistencies.");
		}
		
		if(ol.size() == 0){
			throw new IllegalStateException("Property with name ("+tokenName.name()+") not found.");
		}
		
		AppStateToken ast = ol.get(0);
		ast.setValue(value);
		ast.setLastEditDate(System.currentTimeMillis());
		allAppStateTokens.update(ast);
	}
	
	/** Registers a new token to manage the specified variable state (by token name) of App.
	 * Throws IllegalArgumentException if tokenName or description is not provided or if name is not unique i.e. already exists in system.
	 * @param tokenName
	 * @param defaultValue
	 * @param description
	 * @return The newly registered token. 
	 * 
	 */
	public AppStateToken registerAppStateToken(Enum<?> tokenName, Object defaultValue, String description) {
		if(tokenName == null || StringUtils.isEmptyOrWhitespaceOnly(description)){
			throw new IllegalArgumentException("Token name and description must be provided");
		}
		
		if(allAppStateTokens.findByName(tokenName.name()).size() > 0){
			throw new IllegalArgumentException("Token with given name ("+tokenName.name()+") already exists.");
		}
		AppStateToken token = new AppStateToken(tokenName.name(), defaultValue, 0L, description);
		allAppStateTokens.add(token);
		return token;
	}
}
