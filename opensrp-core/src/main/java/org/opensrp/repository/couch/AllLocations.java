package org.opensrp.repository.couch;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Location;
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
	public Location findByLocationId(String locationId) {
		List<Location> locations = queryView("by_locationId", locationId);
		if (locations == null || locations.isEmpty()) {
			return null;
		}
		return locations.get(0);
	}

	@View(name = "all_locations", map = "function(doc) { if (doc.type === 'Location') { emit(doc.locationId); } }")
	public List<Location> findAllLocations() {
		return db.queryView(createQuery("all_locations").includeDocs(true),
				Location.class);
	}

	@View(name = "all_locations_by_LocationIDs", map = "function(doc) { if (doc.type === 'Location' && doc.locationId) { emit(doc.locationId); } }")
	public List<Location> findAllLocationByIds(List<String> Ids) {
		return db.queryView(createQuery("all_locations_by_LocationIDs").keys(Ids)
				.includeDocs(true), Location.class);
	}

}
