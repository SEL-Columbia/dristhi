package org.opensrp.service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.Address;
import org.opensrp.domain.Client;
import org.opensrp.repository.AllClients;
import org.opensrp.util.DateTimeTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@Service
public class ClientService {
	private final AllClients allClients;
	
	@Autowired
	public ClientService(AllClients allClients)
	{
		this.allClients = allClients;
	}
	
	public Client getByBaseEntityId(String baseEntityId)
	{
		return allClients.findByBaseEntityId(baseEntityId);
	}
	
	public List<Client> findAllClients() {
		return allClients.findAllClients();
	}
	
	public List<Client> findAllByIdentifier(String identifier) {
		return allClients.findAllByIdentifier(identifier);
	}

	public List<Client> findAllByIdentifier(String identifierType, String identifier) {
		return allClients.findAllByIdentifier(identifierType, identifier);
	}
	
	public List<Client> findAllByAttribute(String attributeType, String attribute) {
		return allClients.findAllByAttribute(attributeType, attribute);
	}
	
	public List<Client> findAllByMatchingName(String nameMatches) {
		return allClients.findAllByMatchingName(nameMatches);
	}
	
	public List<Client> findByCriteria(String nameLike, String gender, DateTime birthdate, DateTime deathdate, 
			String attributeType, String attributeValue, 
			String addressType, String country, String stateProvince, String cityVillage, String countyDistrict, 
			String  subDistrict, String town, String subTown) {
		return allClients.findByCriteria(nameLike, gender, birthdate, deathdate, attributeType, attributeValue, addressType, country, stateProvince, cityVillage, countyDistrict, subDistrict, town, subTown);//db.queryView(q.includeDocs(true), Client.class);
	}
	
	public List<Client> findByCriteria(String nameLike, String gender, DateTime birthdate, DateTime deathdate, 
			String attributeType, String attributeValue){
		return allClients.findByCriteria(nameLike, gender, birthdate, deathdate, attributeType, attributeValue, null, null, null, null, null, null, null, null);
	}
	
	public List<Client> findByCriteria(String addressType, String country, String stateProvince, String cityVillage, String countyDistrict, 
			String  subDistrict, String town, String subTown) {
		return allClients.findByCriteria(null, null, null, null, null, null, addressType, country, stateProvince, cityVillage, countyDistrict, subDistrict, town, subTown);
	}
	
	public Client addClient(Client client)
	{
		Client c = findClient(client);
		if(c != null){
			throw new IllegalArgumentException("A client already exists with given list of identifiers. Consider updating data.["+c+"]");
		}
		
		client.setDateCreated(new Date());
		allClients.add(client);
		return client;
	}
	
	public Client findClient(Client client){
		// find by auto assigned entity id
		Client c = allClients.findByBaseEntityId(client.getBaseEntityId());
		if(c != null){
			return c;
		}
		
		// if not found find if it is in any identifiers TODO refactor it later
		List<Client> cl = allClients.findAllByIdentifier(client.getBaseEntityId());
		if(cl.size() > 1){
			throw new IllegalArgumentException("Multiple clients with identifier "+client.getBaseEntityId()+" exist.");
		}
		else if(cl.size() != 0){
			return cl.get(0);
		}
		
		//still not found!! search by generic identifiers
		
		for (String idt : client.getIdentifiers().keySet()) {
			cl = allClients.findAllByIdentifier(idt, client.getIdentifier(idt));
			if(cl.size() > 1){
				throw new IllegalArgumentException("Multiple clients with identifier type "+idt+" and ID "+client.getIdentifier(idt)+" exist.");
			}
			else if(cl.size() != 0){
				return cl.get(0); 
			}
		}
		return c;
	}
	
	public Client find(String uniqueId){
		// find by document id
		Client c = allClients.get(uniqueId);
		if(c != null){
			return c;
		}
		
		// find by auto assigned entity id
		c = allClients.findByBaseEntityId(uniqueId);
		if(c != null){
			return c;
		}
		
		// if not found find if it is in any identifiers TODO refactor it later
		List<Client> cl = allClients.findAllByIdentifier(uniqueId);
		if(cl.size() > 1){
			throw new IllegalArgumentException("Multiple clients with identifier "+uniqueId+" exist.");
		}
		else if(cl.size() != 0){
			return cl.get(0);
		}
		
		return c;
	}
	
	public void updateClient(Client updatedClient) throws JSONException
	{
		// If update is on original entity
		if(updatedClient.isNew()){
			throw new IllegalArgumentException("Client to be updated is not persisted domain object. Update database object instead of new pojo");
		}
		
		if(findClient(updatedClient) == null){
			throw new IllegalArgumentException("No client found with given list of identifiers. Consider adding new!");
		}
		
		allClients.update(updatedClient);
	}
	
	public void mergeClient(Client updatedClient) throws JSONException
	{
		Client original = findClient(updatedClient);
		if(original == null){
			throw new IllegalArgumentException("No client found with given list of identifiers. Consider adding new!");
		}
		
		Gson gs = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
		JSONObject originalJo = new JSONObject(gs.toJson(original));

		JSONObject updatedJo = new JSONObject(gs.toJson(updatedClient));
		List<Field> fn = Arrays.asList(Client.class.getDeclaredFields());

		JSONObject mergedJson = new JSONObject();
		if (originalJo.length() > 0) {
			mergedJson = new JSONObject(originalJo, JSONObject.getNames(originalJo));
		}
		if (updatedJo.length() > 0) {
			for (Field key : fn) {
				String jokey = key.getName();
				if(updatedJo.has(jokey)) mergedJson.put(jokey, updatedJo.get(jokey));
			}
		
			original = gs.fromJson(mergedJson.toString(), Client.class);
			
			for (Address a : updatedClient.getAddresses()) {
				if(original.getAddress(a.getAddressType()) == null) original.addAddress(a);
			}
			for (String k : updatedClient.getIdentifiers().keySet()) {
				original.addIdentifier(k, updatedClient.getIdentifier(k));
			}
			for (String k : updatedClient.getAttributes().keySet()) {
				original.addAttribute(k, updatedClient.getAttribute(k));
			}
		}

		allClients.update(original);
	}
}
