package org.opensrp.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewResult;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Client;
import org.opensrp.domain.Provider;
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
	public Client findByBaseEntityId(String baseEntityId) {
		List<Client> clients = queryView("by_baseEntityId", baseEntityId);
		if (clients == null || clients.isEmpty()) {
			return null;
		}
		return clients.get(0);
	}

	@View(name = "all_clients", map = "function(doc) { if (doc.type === 'Client') { emit(doc.baseEntityId); } }")
	public List<Client> findAllClients() {
		return db.queryView(createQuery("all_clients").includeDocs(true),
				Client.class);
	}

	/*@View(name = "all_clients_by_IDs", map = "function(doc) { if (doc.type === 'Client') { emit(doc.baseEntityId); } }")
	public Client findClientByIds(Map<String,String> keyIds) {
		List<ViewResult.Row> rows = db.queryView(createQuery("all_open_pncs")
                .group(true)
                .reduce(true)
                .cacheOk(true)).getRows();
		
		 Map<String, Integer> openMotherCount = new HashMap<>();
	        for (ViewResult.Row row : rows) {
	            openMotherCount.put(row.getKey(), row.getValueAsInt());
	        }
		
		if (clients == null || clients.isEmpty()) {
			return null;
		}
		return clients.get(0);
	}*/
}
