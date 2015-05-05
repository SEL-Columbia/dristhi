package org.opensrp.service;

import java.util.ArrayList;
import java.util.List;

import org.opensrp.api.domain.BaseEntity;
import org.opensrp.api.domain.Client;
import org.opensrp.repository.AllBaseEntities;
import org.opensrp.repository.AllClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ClientService {
	
	private final  AllBaseEntities allBaseEntities;
	private final AllClients allClients;
	
	@Autowired
	public ClientService(AllClients allClients, AllBaseEntities allBaseEntities)
	{
		this.allClients = allClients;
		this.allBaseEntities = allBaseEntities;
	}
	
	public Client getClientByBaseEntityId(String baseEntityId)
	{
		org.opensrp.domain.Client client = allClients.findByBaseEntityId(baseEntityId);
		org.opensrp.domain.BaseEntity baseEntity = allBaseEntities.findByBaseEntityId(baseEntityId);
		
			BaseEntity apiBaseEntity = new BaseEntity()
										   .withFirstName(baseEntity.getFirstName())
										   .withMiddleName(baseEntity.getMiddleName())
										   .withLastName(baseEntity.getLastName())
										   .withGender(baseEntity.getGender())
										   .withBirthdate(baseEntity.getBirthdate(),baseEntity.getBirthdateApprox())
								 		   .withDeathdate(baseEntity.getDeathdate(), baseEntity.getDeathdateApprox());
			
			//#TODO: Have to add User
			/*org.opensrp.domain.User userCreator  = baseEntity.getCreator().withBaseEntityId(baseEntityId);
			
			
							  apiBaseEntity.withCreator(userCreator);
							  apiBaseEntity.withEditor(baseEntity.getEditor());
							  apiBaseEntity.withVoider(baseEntity.getVoider());
							  apiBaseEntity.withDateCreated(baseEntity.getDateCreated());
							  apiBaseEntity.withDateEdited(baseEntity.getDateEdited());
							  apiBaseEntity.withDateVoided(baseEntity.getDateVoided());
							  apiBaseEntity.withVoided(baseEntity.getVoided());
							  apiBaseEntity.withVoidReason(baseEntity.getRevision());
*/			
			
			Client apiClient = new Client()
								   .withBaseEntityId(client.getBaseEntityId())
								   .withBaseEntity(apiBaseEntity)
								   .withIdentifiers(client.getIdentifiers());
		//#TODO: Have to add User	
						/*  apiClient.withCreator(client.getCreator());
						  apiClient.withEditor(client.getEditor());
						  apiClient.withVoider(client.getVoider());
						  apiClient.withDateCreated(client.getDateCreated());
						  apiClient.withDateEdited(client.getDateEdited());
						  apiClient.withDateVoided(client.getDateVoided());
						  apiClient.withVoided(client.getVoided());
						  apiClient.withVoidReason(client.getRevision());*/
			
		return apiClient;
	}
	
	public Client getClientsByIds(String keyId)
	{
		ArrayList<Client> apiClients = new ArrayList<>();
		org.opensrp.domain.Client client = new org.opensrp.domain.Client();//allClients.findClientByIds(keyId);
		
			org.opensrp.domain.BaseEntity baseEntity =  client.getBaseEntity();
			
			BaseEntity apiBaseEntity = new BaseEntity()
										   .withFirstName(baseEntity.getFirstName())
										   .withMiddleName(baseEntity.getMiddleName())
										   .withLastName(baseEntity.getLastName())
										   .withGender(baseEntity.getGender())
										   .withBirthdate(baseEntity.getBirthdate(),baseEntity.getBirthdateApprox())
								 		   .withDeathdate(baseEntity.getDeathdate(), baseEntity.getDeathdateApprox());
			
			//#TODO: Have to add User
			/*org.opensrp.domain.User userCreator  = baseEntity.getCreator().withBaseEntityId(baseEntityId);
			
			
							  apiBaseEntity.withCreator(userCreator);
							  apiBaseEntity.withEditor(baseEntity.getEditor());
							  apiBaseEntity.withVoider(baseEntity.getVoider());
							  apiBaseEntity.withDateCreated(baseEntity.getDateCreated());
							  apiBaseEntity.withDateEdited(baseEntity.getDateEdited());
							  apiBaseEntity.withDateVoided(baseEntity.getDateVoided());
							  apiBaseEntity.withVoided(baseEntity.getVoided());
							  apiBaseEntity.withVoidReason(baseEntity.getRevision());
*/			
			
			Client apiClient = new Client()
								   .withBaseEntityId(client.getBaseEntityId())
								   .withBaseEntity(apiBaseEntity)
								   .withIdentifiers(client.getIdentifiers());
		//#TODO: Have to add User	
						/*  apiClient.withCreator(client.getCreator());
						  apiClient.withEditor(client.getEditor());
						  apiClient.withVoider(client.getVoider());
						  apiClient.withDateCreated(client.getDateCreated());
						  apiClient.withDateEdited(client.getDateEdited());
						  apiClient.withDateVoided(client.getDateVoided());
						  apiClient.withVoided(client.getVoided());
						  apiClient.withVoidReason(client.getRevision());*/
								
							apiClients.add(apiClient);
			
		return apiClient;
	}
	
	public List<Client> getAllClients()
	{
		ArrayList<Client> apiClients = new ArrayList<>();
		List<org.opensrp.domain.Client> clients = allClients.findAllClients();
		
		for(org.opensrp.domain.Client client : clients)
		{
			org.opensrp.domain.BaseEntity baseEntity =  client.getBaseEntity();
			
			BaseEntity apiBaseEntity = new BaseEntity()
										   .withFirstName(baseEntity.getFirstName())
										   .withMiddleName(baseEntity.getMiddleName())
										   .withLastName(baseEntity.getLastName())
										   .withGender(baseEntity.getGender())
										   .withBirthdate(baseEntity.getBirthdate(),baseEntity.getBirthdateApprox())
								 		   .withDeathdate(baseEntity.getDeathdate(), baseEntity.getDeathdateApprox());
			
			//#TODO: Have to add User
			/*org.opensrp.domain.User userCreator  = baseEntity.getCreator().withBaseEntityId(baseEntityId);
			
			
							  apiBaseEntity.withCreator(userCreator);
							  apiBaseEntity.withEditor(baseEntity.getEditor());
							  apiBaseEntity.withVoider(baseEntity.getVoider());
							  apiBaseEntity.withDateCreated(baseEntity.getDateCreated());
							  apiBaseEntity.withDateEdited(baseEntity.getDateEdited());
							  apiBaseEntity.withDateVoided(baseEntity.getDateVoided());
							  apiBaseEntity.withVoided(baseEntity.getVoided());
							  apiBaseEntity.withVoidReason(baseEntity.getRevision());
*/			
			
			Client apiClient = new Client()
								   .withBaseEntityId(client.getBaseEntityId())
								   .withBaseEntity(apiBaseEntity)
								   .withIdentifiers(client.getIdentifiers());
		//#TODO: Have to add User	
						/*  apiClient.withCreator(client.getCreator());
						  apiClient.withEditor(client.getEditor());
						  apiClient.withVoider(client.getVoider());
						  apiClient.withDateCreated(client.getDateCreated());
						  apiClient.withDateEdited(client.getDateEdited());
						  apiClient.withDateVoided(client.getDateVoided());
						  apiClient.withVoided(client.getVoided());
						  apiClient.withVoidReason(client.getRevision());*/
								
							apiClients.add(apiClient);
								
		}
		return apiClients;
	}
	
	public void addClient(Client client)
	{
		org.opensrp.domain.Client domainClient = new org.opensrp.domain.Client()
													.withBaseEntityId(client.getBaseEntityId())
													.withIdentifiers(client.getIdentifiers());
		
												allClients.add(domainClient);
	}
	
	public void updateClient(Client client)
	{
		org.opensrp.domain.Client domainClient = new org.opensrp.domain.Client()
													.withBaseEntityId(client.getBaseEntityId())
													.withIdentifiers(client.getIdentifiers());
		
												allClients.update(domainClient);
	}
}
