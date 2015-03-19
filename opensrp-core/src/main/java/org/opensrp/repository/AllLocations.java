package org.opensrp.repository;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Location;
import org.opensrp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllLocations extends MotechBaseRepository<Location> {

	@Autowired
	protected AllLocations(
			@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(Location.class, db);
	}
	@GenerateView
	public Location findByCaseId(String caseId) {
		List<Location> locations = queryView("by_caseId", caseId);
		if (locations == null || locations.isEmpty()) {
			return null;
		}
		return locations.get(0);
	}

	@View(name = "all_locations", map = "function(doc) { if (doc.type === 'Location') { emit(doc.caseId); } }")
	public List<Location> findAllLocations() {
		return db.queryView(createQuery("all_locations").includeDocs(true),
				Location.class);
	}

	@View(name = "all_users_by_CaseIDs", map = "function(doc) { if (doc.type === 'Location' && doc.caseId) { emit(doc.caseId); } }")
	public List<Location> findAll(List<String> ecIds) {
		return db.queryView(createQuery("all_users_by_CaseIDs").keys(ecIds)
				.includeDocs(true), Location.class);
	}

}
