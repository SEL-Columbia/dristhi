package org.opensrp.service;

import java.util.List;

import org.opensrp.domain.Client;
import org.opensrp.repository.AllClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
	
	public void addClient(Client client)
	{
		allClients.add(client);
	}
	
	public void updateClient(Client client)
	{
		allClients.update(client);
	}
}
