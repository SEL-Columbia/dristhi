package org.opensrp.repository;

import java.util.List;

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
	protected AllClients(
			@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(Client.class, db);
	}

	@GenerateView
	public Client findByCaseId(String caseId) {
		List<Client> clients = queryView("by_caseId", caseId);
		if (clients == null || clients.isEmpty()) {
			return null;
		}
		return clients.get(0);
	}

	public boolean exists(String caseId) {
		return findByCaseId(caseId) != null;
	}

	@View(name = "all_clients", map = "function(doc) { if (doc.type === 'Client') { emit(doc.caseId); } }")
	public List<Client> findAllClients() {
		return db.queryView(createQuery("all_clients").includeDocs(true),
				Client.class);
	}

	@View(name = "all_clients_by_CaseIDs", map = "function(doc) { if (doc.type === 'Client' && doc.caseId) { emit(doc.caseId); } }")
	public List<Client> findAll(List<String> ecIds) {
		return db.queryView(createQuery("all_clients_by_CaseIDs").keys(ecIds)
				.includeDocs(true), Client.class);
	}
}
