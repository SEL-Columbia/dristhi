package org.opensrp.service;

import java.util.ArrayList;
import java.util.List;

import org.opensrp.api.domain.BaseEntity;
import org.opensrp.api.domain.User;
import org.opensrp.repository.AllBaseEntities;
import org.opensrp.repository.AllUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	private final AllUsers allUsers;
	private final  AllBaseEntities allBaseEntities;
	
	@Autowired
	public UserService(AllUsers allUsers, AllBaseEntities allBaseEntities)
	{
		this.allUsers = allUsers;
		this.allBaseEntities = allBaseEntities;
	}
	
	
	public User getUserByEntityId(String baseEntityId)
	{
		org.opensrp.domain.User user = allUsers.findByBaseEntityId(baseEntityId);
		org.opensrp.domain.BaseEntity baseEntity = allBaseEntities.findByBaseEntityId(baseEntityId);
			
			BaseEntity apiBaseEntity = new BaseEntity()
										   .withFirstName(baseEntity.getFirstName())
										   .withMiddleName(baseEntity.getMiddleName())
										   .withLastName(baseEntity.getLastName())
										   .withGender(baseEntity.getGender())
										   .withBirthdate(baseEntity.getBirthdate(),baseEntity.getBirthdateApprox())
										   .withDeathdate(baseEntity.getDeathdate(), baseEntity.getDeathdateApprox());

					/*		apiBaseEntity.withCreator(baseEntity.getCreator());
							apiBaseEntity.withEditor(baseEntity.getEditor());
							apiBaseEntity.withVoider(baseEntity.getVoider());
							apiBaseEntity.withDateCreated(baseEntity.getDateCreated());
							apiBaseEntity.withDateEdited(baseEntity.getDateEdited());
							apiBaseEntity.withDateVoided(baseEntity.getDateVoided());
							apiBaseEntity.withVoided(baseEntity.getVoided());
							apiBaseEntity.withVoidReason(baseEntity.getRevision());*/
							
			
			User apiUser = new User()
						  .withUsername(user.getUsername())
						  .withPassword(user.getPassword())
						  .withSalt(user.getSalt())
						  .withStatus(user.getStatus())
						  .withPermissions(user.getPermissions())
						  .withRoles(user.getRoles())
						  .withBaseEntityId(user.getBaseEntityId())
						  .withBaseEntity(apiBaseEntity);
						 
				/*	apiUser.withCreator(user.getCreator());
					apiUser.withEditor(user.getEditor());
					apiUser.withVoider(user.getVoider());
					apiUser.withDateCreated(user.getDateCreated());
					apiUser.withDateEdited(user.getDateEdited());
					apiUser.withDateVoided(user.getDateVoided());
					apiUser.withVoided(user.getVoided());
					apiUser.withVoidReason(user.getRevision());*/
						  
					
		return apiUser;
		
	}
	public List<User> getAllUsers()
	{
		ArrayList<User> apiUsers = new ArrayList<>();
		List<org.opensrp.domain.User> users = allUsers.findAllUsers();
		
		for(org.opensrp.domain.User user : users)
		{
			org.opensrp.domain.BaseEntity baseEntity =  user.getBaseEntity();
			
			BaseEntity apiBaseEntity = new BaseEntity()
										   .withFirstName(baseEntity.getFirstName())
										   .withMiddleName(baseEntity.getMiddleName())
										   .withLastName(baseEntity.getLastName())
										   .withGender(baseEntity.getGender())
										   .withBirthdate(baseEntity.getBirthdate(),baseEntity.getBirthdateApprox())
										   .withDeathdate(baseEntity.getDeathdate(), baseEntity.getDeathdateApprox());

					/*		apiBaseEntity.withCreator(baseEntity.getCreator());
							apiBaseEntity.withEditor(baseEntity.getEditor());
							apiBaseEntity.withVoider(baseEntity.getVoider());
							apiBaseEntity.withDateCreated(baseEntity.getDateCreated());
							apiBaseEntity.withDateEdited(baseEntity.getDateEdited());
							apiBaseEntity.withDateVoided(baseEntity.getDateVoided());
							apiBaseEntity.withVoided(baseEntity.getVoided());
							apiBaseEntity.withVoidReason(baseEntity.getRevision());*/
							
			
			User apiUser = new User()
						  .withUsername(user.getUsername())
						  .withPassword(user.getPassword())
						  .withSalt(user.getSalt())
						  .withStatus(user.getStatus())
						  .withPermissions(user.getPermissions())
						  .withRoles(user.getRoles())
						  .withBaseEntityId(user.getBaseEntityId())
						  .withBaseEntity(apiBaseEntity);
						 
				/*	apiUser.withCreator(user.getCreator());
					apiUser.withEditor(user.getEditor());
					apiUser.withVoider(user.getVoider());
					apiUser.withDateCreated(user.getDateCreated());
					apiUser.withDateEdited(user.getDateEdited());
					apiUser.withDateVoided(user.getDateVoided());
					apiUser.withVoided(user.getVoided());
					apiUser.withVoidReason(user.getRevision());*/
						  
			apiUsers.add(apiUser);
					
			
		}
		
		return apiUsers;
		
	}
	
	public void addUser(User user)
	{
		org.opensrp.domain.User domainUser = new org.opensrp.domain.User()
											.withUsername(user.getUsername())
											.withPassword(user.getPassword())
											.withSalt(user.getSalt())
											.withStatus(user.getStatus())
											.withPermissions(user.getPermissions())
											.withRoles(user.getRoles())
											.withBaseEntityId(user.getBaseEntityId());
		allUsers.add(domainUser);
	
	}

	public void updateUser(User user)
	{
		org.opensrp.domain.User domainUser = new org.opensrp.domain.User()
											.withUsername(user.getUsername())
											.withPassword(user.getPassword())
											.withSalt(user.getSalt())
											.withStatus(user.getStatus())
											.withPermissions(user.getPermissions())
											.withRoles(user.getRoles())
											.withBaseEntityId(user.getBaseEntityId());
		allUsers.update(domainUser);
	
	}
}
