package org.opensrp.connector.openmrs;

import java.net.URI;
import java.net.URISyntaxException;

import org.ict4h.atomfeed.client.repository.datasource.WebClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.opensrp.connector.HttpUtil;
import org.opensrp.connector.openmrs.service.OpenmrsService;
import org.springframework.stereotype.Component;

@Component
public interface AtomFeedSource {
	public String category();
}
