package org.opensrp.service;

import java.util.ArrayList;
import java.util.List;

import org.opensrp.api.domain.BaseEntity;
import org.opensrp.api.domain.Provider;
import org.opensrp.repository.AllBaseEntities;
import org.opensrp.repository.AllProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProviderService {
	
	private final  AllBaseEntities allBaseEntities;
	private final AllProviders allProviders;
	
	@Autowired
	public ProviderService(AllProviders allProviders, AllBaseEntities allBaseEntities)
	{
		this.allProviders = allProviders;
		this.allBaseEntities = allBaseEntities;
	}
	
	public Provider getProviderByBaseEntityId(String baseEntityId)
	{
		
		org.opensrp.domain.Provider provider = allProviders.findByBaseEntityId(baseEntityId);
		org.opensrp.domain.BaseEntity baseEntity = allBaseEntities.findByBaseEntityId(baseEntityId);
			
			BaseEntity apiBaseEntity = new BaseEntity()
										   .withFirstName(baseEntity.getFirstName())
										   .withMiddleName(baseEntity.getMiddleName())
										   .withLastName(baseEntity.getLastName())
										   .withGender(baseEntity.getGender())
										   .withBirthdate(baseEntity.getBirthdate(),baseEntity.getBirthdateApprox())
										   .withDeathdate(baseEntity.getDeathdate(), baseEntity.getDeathdateApprox());
			//#TODO: Have to add User
			
						/*	  apiBaseEntity.withCreator(baseEntity.getCreator());
							  apiBaseEntity.withEditor(baseEntity.getEditor());
							  apiBaseEntity.withVoider(baseEntity.getVoider());
							  apiBaseEntity.withDateCreated(baseEntity.getDateCreated());
							  apiBaseEntity.withDateEdited(baseEntity.getDateEdited());
							  apiBaseEntity.withDateVoided(baseEntity.getDateVoided());
							  apiBaseEntity.withVoided(baseEntity.getVoided());
							  apiBaseEntity.withVoidReason(baseEntity.getRevision());*/
							  
		    Provider apiProvider = new Provider()
							     .withBaseEntityId(provider.getBaseEntityId())
							     .withBaseEntity(apiBaseEntity)
							     .withIdentifiers(provider.getIdentifiers());
		    //#TODO: Have to add User
		    		/*  apiProvider.withCreator(provider.getCreator());
		    		  apiProvider.withEditor(provider.getEditor());
		    		  apiProvider.withVoider(provider.getVoider());
		    		  apiProvider.withDateCreated(provider.getDateCreated());
		    		  apiProvider.withDateEdited(provider.getDateEdited());
		    		  apiProvider.withDateVoided(provider.getDateVoided());
		    		  apiProvider.withVoided(provider.getVoided());
		    		  apiProvider.withVoidReason(provider.getRevision());*/
							
			
		return apiProvider;

	}

	public List<Provider> getAllProviders()
	{
		ArrayList<Provider> apiProviders = new ArrayList<>();
		
		List<org.opensrp.domain.Provider> providers = allProviders.findAllProviders();
		
		for(org.opensrp.domain.Provider provider : providers)
		{
			org.opensrp.domain.BaseEntity baseEntity =  provider.getBaseEntity();
			
			BaseEntity apiBaseEntity = new BaseEntity()
										   .withFirstName(baseEntity.getFirstName())
										   .withMiddleName(baseEntity.getMiddleName())
										   .withLastName(baseEntity.getLastName())
										   .withGender(baseEntity.getGender())
										   .withBirthdate(baseEntity.getBirthdate(),baseEntity.getBirthdateApprox())
										   .withDeathdate(baseEntity.getDeathdate(), baseEntity.getDeathdateApprox());
			//#TODO: Have to add User
			
						/*	  apiBaseEntity.withCreator(baseEntity.getCreator());
							  apiBaseEntity.withEditor(baseEntity.getEditor());
							  apiBaseEntity.withVoider(baseEntity.getVoider());
							  apiBaseEntity.withDateCreated(baseEntity.getDateCreated());
							  apiBaseEntity.withDateEdited(baseEntity.getDateEdited());
							  apiBaseEntity.withDateVoided(baseEntity.getDateVoided());
							  apiBaseEntity.withVoided(baseEntity.getVoided());
							  apiBaseEntity.withVoidReason(baseEntity.getRevision());*/
							  
		    Provider apiProvider = new Provider()
							     .withBaseEntityId(provider.getBaseEntityId())
							     .withBaseEntity(apiBaseEntity)
							     .withIdentifiers(provider.getIdentifiers());
		    //#TODO: Have to add User
		    		/*  apiProvider.withCreator(provider.getCreator());
		    		  apiProvider.withEditor(provider.getEditor());
		    		  apiProvider.withVoider(provider.getVoider());
		    		  apiProvider.withDateCreated(provider.getDateCreated());
		    		  apiProvider.withDateEdited(provider.getDateEdited());
		    		  apiProvider.withDateVoided(provider.getDateVoided());
		    		  apiProvider.withVoided(provider.getVoided());
		    		  apiProvider.withVoidReason(provider.getRevision());*/
							
		     apiProviders.add(apiProvider);			  
			
		}
		
		return apiProviders;

	}
	
	public void addProvider(Provider provider)
	{
		org.opensrp.domain.Provider domainProvider = new org.opensrp.domain.Provider()
														.withBaseEntityId(provider.getBaseEntityId())
														.withIdentifiers(provider.getIdentifiers());
		allProviders.add(domainProvider);
		
	}
	public void uodateProvider(Provider provider)
	{
		
		org.opensrp.domain.Provider domainProvider = new org.opensrp.domain.Provider()
														.withBaseEntityId(provider.getBaseEntityId())
														.withIdentifiers(provider.getIdentifiers());
		allProviders.update(domainProvider);
		
	}
	
}
