package org.opensrp.service;

import java.util.ArrayList;
import java.util.List;

import org.opensrp.api.domain.BaseEntity;
import org.opensrp.repository.AllBaseEntities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseEntityService {

	private final AllBaseEntities allBaseEntities;

	@Autowired
	public BaseEntityService(AllBaseEntities allBaseEntities ) {
		this.allBaseEntities = allBaseEntities;
	}
	 public List<BaseEntity> getAllBaseEntities()
	 {
		 ArrayList<BaseEntity> apiBaseEntities = new ArrayList<>();
		 List<org.opensrp.domain.BaseEntity> baseEntities = allBaseEntities.findAllBaseEntities();
		 
		 for(org.opensrp.domain.BaseEntity baseEntity : baseEntities)
		 {
			 BaseEntity apiBaseEntity = new BaseEntity()
			 							  .withFirstName(baseEntity.getFirstName())
			 							  .withMiddleName(baseEntity.getMiddleName())
			 							  .withLastName(baseEntity.getLastName())
			 							  .withGender(baseEntity.getGender())
			 							  .withName(baseEntity.getFirstName(), baseEntity.getMiddleName(), baseEntity.getLastName())
			 							  .withBirthdate(baseEntity.getBirthdate(), baseEntity.getBirthdateApprox())
			 							  .withDeathdate(baseEntity.getDeathdate(), baseEntity.getDeathdateApprox())
			 							//  .withAddresses(baseEntity.getAddresses())
			 							  .withAttributes(baseEntity.getAttributes());
			 
			 
			 //#TODO: Have to add User
			 							  
						/*	 apiBaseEntity.withCreator(baseEntity.getCreator());
							 apiBaseEntity.withEditor(baseEntity.getEditor());
							 apiBaseEntity.withVoider(baseEntity.getVoider());
							 apiBaseEntity.withDateCreated(baseEntity.getDateCreated());
							 apiBaseEntity.withDateEdited(baseEntity.getDateEdited());
							 apiBaseEntity.withDateVoided(baseEntity.getDateVoided());
							 apiBaseEntity.withVoided(baseEntity.getVoided());
							 apiBaseEntity.withVoidReason(baseEntity.getRevision());*/
			 		
			apiBaseEntities.add(apiBaseEntity);
			 
		 }
		 
		 return apiBaseEntities;
		 
	 }
	 
	 public void addBaseEntity(BaseEntity baseEntity)
	 {
		 org.opensrp.domain.BaseEntity domainBaseEntity = new org.opensrp.domain.BaseEntity()
		 												.withFirstName(baseEntity.getFirstName())
		 												.withMiddleName(baseEntity.getMiddleName())
		 												.withLastName(baseEntity.getLastName())
		 												.withGender(baseEntity.getGender())
		 												.withBirthdate(baseEntity.getBirthdate(), baseEntity.getBirthdateApprox())
		 												.withDeathdate(baseEntity.getDeathdate(), baseEntity.getDeathdateApprox())
		 												//.withAddresses(baseEntity.getAddresses())
		 												.withAttributes(baseEntity.getAttributes());
		 allBaseEntities.add(domainBaseEntity);
	 }
	 
	 public void updateBaseEntity(BaseEntity baseEntity)
	 {
		 org.opensrp.domain.BaseEntity domainBaseEntity = new org.opensrp.domain.BaseEntity()
		 												.withFirstName(baseEntity.getFirstName())
		 												.withMiddleName(baseEntity.getMiddleName())
		 												.withLastName(baseEntity.getLastName())
		 												.withGender(baseEntity.getGender())
		 												.withBirthdate(baseEntity.getBirthdate(), baseEntity.getBirthdateApprox())
		 												.withDeathdate(baseEntity.getDeathdate(), baseEntity.getDeathdateApprox())
		 											//	.withAddresses(baseEntity.getAddresses())
		 												.withAttributes(baseEntity.getAttributes());
		 allBaseEntities.update(domainBaseEntity);
	 }

}
