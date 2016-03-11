package org.opensrp.connector.atomfeed;

import java.net.URI;
import java.net.URISyntaxException;

import org.ektorp.CouchDbConnector;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllMarkersCouchImpl implements AllMarkers {

	private CouchDbConnector db;
	@Autowired
	public AllMarkersCouchImpl(@Qualifier(OpenmrsConstants.ATOMFEED_DATABASE_CONNECTOR) CouchDbConnector db) {
		this.db = db;
	}

	@Override
	public Marker get(URI feedUri) {
		org.opensrp.connector.atomfeed.domain.Marker marker = getDocument(feedUri);
		try {
			return marker == null ? null : marker.toMarker();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private org.opensrp.connector.atomfeed.domain.Marker getDocument(URI feedUri) {
		return db.get(org.opensrp.connector.atomfeed.domain.Marker.class, feedUri.toString());
	}

	@Override
	public void put(URI feedUri, String entryId, URI entryFeedUri) {
		org.opensrp.connector.atomfeed.domain.Marker doc = getDocument(feedUri);
		if (doc != null) {
			doc.setLastReadEntryId(entryId);
			doc.setFeedURIForLastReadEntry(entryFeedUri.toString());
			db.update(doc);
		} else {
			doc = new org.opensrp.connector.atomfeed.domain.Marker(new Marker(feedUri, entryId, entryFeedUri));
			db.create(doc);
		}
	}

}
