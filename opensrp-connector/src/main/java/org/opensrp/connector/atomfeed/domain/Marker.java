package org.opensrp.connector.atomfeed.domain;

import java.net.URI;
import java.net.URISyntaxException;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type == 'Marker'")
public class Marker extends MotechBaseDataObject{
	private String feedUri;
    private String lastReadEntryId;
    private String feedURIForLastReadEntry;

    private Marker() {
		
	}
    
    public Marker(org.ict4h.atomfeed.client.domain.Marker marker) {
		this.feedUri = marker.getFeedUri().toString();
		this.lastReadEntryId = marker.getLastReadEntryId();
		this.feedURIForLastReadEntry = marker.getFeedURIForLastReadEntry().toString();
	}

	public String getFeedUri() {
		return feedUri;
	}

	public void setFeedUri(String feedUri) {
		this.feedUri = feedUri;
	}

	public String getLastReadEntryId() {
		return lastReadEntryId;
	}

	public void setLastReadEntryId(String lastReadEntryId) {
		this.lastReadEntryId = lastReadEntryId;
	}

	public String getFeedURIForLastReadEntry() {
		return feedURIForLastReadEntry;
	}

	public void setFeedURIForLastReadEntry(String feedURIForLastReadEntry) {
		this.feedURIForLastReadEntry = feedURIForLastReadEntry;
	}

	public org.ict4h.atomfeed.client.domain.Marker toMarker() throws URISyntaxException {
		return new org.ict4h.atomfeed.client.domain.Marker(new URI(feedUri), 
				lastReadEntryId, new URI(feedURIForLastReadEntry));
	}
    
 }
