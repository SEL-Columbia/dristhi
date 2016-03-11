package org.opensrp.connector.atomfeed;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.connector.atomfeed.domain.Marker;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.StringUtils;

@Repository
public class AllMarkersCouchImpl extends MotechBaseRepository<Marker> implements AllMarkers {

	private CouchDbConnector db;
	@Autowired
	public AllMarkersCouchImpl(@Qualifier(OpenmrsConstants.ATOMFEED_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(Marker.class, db);
		this.db = db;
	}

	@GenerateView
	public Marker findByfeedUri(String feedUri) {
		if(StringUtils.isEmptyOrWhitespaceOnly(feedUri))
			return null;
		List<Marker> ol = queryView("by_feedUri", feedUri);
		if (ol == null || ol.isEmpty()) {
			return null;
		}
		return ol.get(0);
	}

	@View(name = "all_markers", map = "function(doc) { if (doc.type === 'Marker') { emit(doc.feedUri); } }")
	public List<Marker> findAllMarkers() {
		return db.queryView(createQuery("all_markers").includeDocs(true), Marker.class);
	}
	
	@Override
	public org.ict4h.atomfeed.client.domain.Marker get(URI feedUri) {
		Marker marker = findByfeedUri(feedUri.toString());
		try {
			return marker == null ? null : marker.toMarker();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void put(URI feedUri, String entryId, URI entryFeedUri) {
		Marker doc = findByfeedUri(feedUri.toString());
		if (doc != null) {
			doc.setLastReadEntryId(entryId);
			doc.setFeedURIForLastReadEntry(entryFeedUri.toString());
			update(doc);
		} else {
			doc = new Marker(new org.ict4h.atomfeed.client.domain.Marker(feedUri, entryId, entryFeedUri));
			add(doc);
		}
	}

}
