package org.opensrp.repository;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllClients extends MotechBaseRepository<Client> {

	@Autowired
	protected AllClients(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(Client.class, db);
	}

	@GenerateView
	public Client findByBaseEntityId(String baseEntityId) {
		List<Client> clients = queryView("by_baseEntityId", baseEntityId);
		if (clients == null || clients.isEmpty()) {
			return null;
		}
		return clients.get(0);
	}

	@View(name = "all_clients", map = "function(doc) { if (doc.type === 'Client') { emit(doc.baseEntityId); } }")
	public List<Client> findAllClients() {
		return db.queryView(createQuery("all_clients").includeDocs(true), Client.class);
	}
	
	@View(name = "all_clients_by_identifier", map = "function(doc) {if (doc.type === 'Client') {for(var key in doc.identifiers) {emit([doc.identifiers[key]]);}}}")
	public List<Client> findAllByIdentifier(String identifier) {
		return db.queryView(createQuery("all_clients_by_identifier").key(identifier).includeDocs(true), Client.class);
	}

	@View(name = "all_clients_by_identifier_of_type", map = "function(doc) {if (doc.type === 'Client') {for(var key in doc.identifiers) {emit([key, doc.identifiers[key]]);}}}")
	public List<Client> findAllByIdentifier(String identifierType, String identifier) {
		ComplexKey ckey = ComplexKey.of(identifierType, identifier);
		return db.queryView(createQuery("all_clients_by_identifier_of_type").key(ckey).includeDocs(true), Client.class);
	}
	
	@View(name = "all_clients_by_attribute_of_type", map = "function(doc) {if (doc.type === 'Client') {for(var key in doc.attributes) {emit([key, doc.attributes[key]]);}}}")
	public List<Client> findAllByAttribute(String attributeType, String attribute) {
		ComplexKey ckey = ComplexKey.of(attributeType, attribute);
		return db.queryView(createQuery("all_clients_by_attribute_of_type").key(ckey).includeDocs(true), Client.class);
	}
}
