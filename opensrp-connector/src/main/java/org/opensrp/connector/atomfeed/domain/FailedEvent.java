package org.opensrp.connector.atomfeed.domain;

import java.util.Date;

import org.ektorp.support.TypeDiscriminator;
import org.ict4h.atomfeed.client.domain.Event;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("type == 'FailedEvent'")
public class FailedEvent extends MotechBaseDataObject{
    private long failedAt;
    private String feedUri;
    private int retries;
    private String errorMessage;
    private int errorHashCode;
    private String title;
    private String eventId;
    private String eventContent;

    private FailedEvent() {
		
	}
    
    public FailedEvent(String feedUri, org.ict4h.atomfeed.client.domain.FailedEvent event, 
    		String errorMessage, int retries) {
        if (feedUri == null || feedUri.trim().equals("") || event == null)
            throw new IllegalArgumentException("Arguments provided should not be null.");
        this.errorHashCode = event.hashCode();
        this.eventId = event.getEventId();
        this.eventContent = event.getEvent().getContent();
        this.errorMessage = errorMessage;
        this.feedUri = feedUri;
        this.failedAt = new Date().getTime();
        this.retries = retries;
    }
    
    public long getFailedAt() {
        return failedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getFeedUri() {
        return feedUri;
    }

    public void setFailedAt(long failedAt) {
        this.failedAt = failedAt;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public void incrementRetryCount() {
        this.retries++;
    }

    public int getErrorHashCode() {
		return errorHashCode;
	}

	public void setErrorHashCode(int errorHashCode) {
		this.errorHashCode = errorHashCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEventId() {
		return eventId;
	}

	public String getEventContent() {
		return eventContent;
	}

	public void setEventContent(String eventContent) {
		this.eventContent = eventContent;
	}

	public void setFeedUri(String feedUri) {
		this.feedUri = feedUri;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public org.ict4h.atomfeed.client.domain.FailedEvent toFailedEvent() {
		Event event = new Event(eventId, eventContent, title, feedUri, new Date(failedAt));
		return new org.ict4h.atomfeed.client.domain.FailedEvent(feedUri, event , errorMessage, failedAt, retries);
	}
	
	@Override
    public String toString() {
        return String.format("FailedEvent{failedAt=%d, errorMessage='%s', feedUri='%s'}", failedAt, errorMessage, feedUri);
    }

}
