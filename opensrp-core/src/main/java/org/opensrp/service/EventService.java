package org.opensrp.service;

import java.util.ArrayList;
import java.util.List;

import org.opensrp.api.domain.BaseEntity;
import org.opensrp.api.domain.Client;
import org.opensrp.api.domain.Event;
import org.opensrp.api.domain.Obs;
import org.opensrp.repository.AllBaseEntities;
import org.opensrp.repository.AllEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

	private final AllEvents allEvents;
	private final  AllBaseEntities allBaseEntities;
	
	@Autowired
	public EventService(AllEvents allEvents, AllBaseEntities allBaseEntities)
	{
		this.allEvents = allEvents;
		this.allBaseEntities = allBaseEntities;
	}
	
	public Event getEventByBaseEntityId(String baseEntityId)
	{
		org.opensrp.domain.Event event = allEvents.findByBaseEntityId(baseEntityId);
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
			List<org.opensrp.domain.Obs> domainObss = event.getObs();
			List<Obs> obss = new ArrayList<>();
			for(org.opensrp.domain.Obs domainObs : domainObss)
			{
				Obs obs = new Obs()
								.withFieldDataType(domainObs.getFieldDataType())
								.withFieldCode(domainObs.getFieldCode())
								.withParentCode(domainObs.getParentCode())
								.withFormSubmissionField(domainObs.getFormSubmissionField())
								.withValue(domainObs.getValue())
								.withComments(domainObs.getComments());
				
				
				obss.add(obs);
			}
			
			Event apiEvent = new Event()
								   .withBaseEntityId(event.getBaseEntityId())
								   .withBaseEntity(apiBaseEntity)
								   .withLocationId(event.getLocationId())
								   .withEventType(event.getEventType())
								   .withEventDate(event.getEventDate())
								   .withProviderId(event.getProviderId())
								   .withFormSubmissionId(event.getFormSubmissionId())
								   .withObs(obss);
			
		//#TODO: Have to add User	
						/*  apiClient.withCreator(client.getCreator());
						  apiClient.withEditor(client.getEditor());
						  apiClient.withVoider(client.getVoider());
						  apiClient.withDateCreated(client.getDateCreated());
						  apiClient.withDateEdited(client.getDateEdited());
						  apiClient.withDateVoided(client.getDateVoided());
						  apiClient.withVoided(client.getVoided());
						  apiClient.withVoidReason(client.getRevision());*/
			
		return apiEvent;
	}
	
	public void addEvent(Event event)
	{
		List<Obs> apiObss = event.getObs();
		List<org.opensrp.domain.Obs> obss = new ArrayList<>();
		for(Obs apiObs : apiObss)
		{
			org.opensrp.domain.Obs obs = new org.opensrp.domain.Obs()
											.withFieldDataType(apiObs.getFieldDataType())
											.withFieldCode(apiObs.getFieldCode())
											.withParentCode(apiObs.getParentCode())
											.withFormSubmissionField(apiObs.getFormSubmissionField())
											.withValue(apiObs.getValue())
											.withComments(apiObs.getComments());
			
			
			obss.add(obs);
		}
		
		org.opensrp.domain.Event domainEvent = new org.opensrp.domain.Event()
													.withBaseEntityId(event.getBaseEntityId())
													.withLocationId(event.getLocationId())
													.withEventType(event.getEventType())
													.withEventDate(event.getEventDate())
													.withFormSubmissionId(event.getFormSubmissionId())
													.withProviderId(event.getProviderId())
													.withObs(obss);
		
												allEvents.add(domainEvent);				
	}
	
	public void updateEvent(Event event)
	{
		List<Obs> apiObss = event.getObs();
		List<org.opensrp.domain.Obs> obss = new ArrayList<>();
		for(Obs apiObs : apiObss)
		{
			org.opensrp.domain.Obs obs = new org.opensrp.domain.Obs()
											.withFieldDataType(apiObs.getFieldDataType())
											.withFieldCode(apiObs.getFieldCode())
											.withParentCode(apiObs.getParentCode())
											.withFormSubmissionField(apiObs.getFormSubmissionField())
											.withValue(apiObs.getValue())
											.withComments(apiObs.getComments());
			
			
								obss.add(obs);
		}
		
		org.opensrp.domain.Event domainEvent = new org.opensrp.domain.Event()
													.withBaseEntityId(event.getBaseEntityId())
													.withLocationId(event.getLocationId())
													.withEventType(event.getEventType())
													.withEventDate(event.getEventDate())
													.withFormSubmissionId(event.getFormSubmissionId())
													.withProviderId(event.getProviderId())
													.withObs(obss);
		
												allEvents.update(domainEvent);					
	}
}
