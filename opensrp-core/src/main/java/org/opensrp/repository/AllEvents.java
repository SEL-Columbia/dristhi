package org.opensrp.repository;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllEvents extends MotechBaseRepository<Event>{
	
	
	@Autowired
	protected AllEvents(
			@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(Event.class, db);
	}
	
	@GenerateView
	public Event findByBaseEntityId(String baseEntityId) {
		List<Event> events = queryView("by_baseEntityId", baseEntityId);
		if (events == null || events.isEmpty()) {
			return null;
		}
		return events.get(0);
	}
	@View(name = "all_events", map = "function(doc) { if (doc.type === 'Event') { emit(doc.baseEntityId); } }")
	public List<Event> findAllEvents() {
		return db.queryView(createQuery("all_events").includeDocs(true),
				Event.class);
	}
	
}
