package org.ei.drishti.listener;

import com.google.gson.Gson;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReportingEventListener {

    private HttpAgent httpAgent;
    private String url;
    private static Logger logger = LoggerFactory.getLogger(ReportingEventListener.class);

    @Autowired
    public ReportingEventListener(HttpAgent httpAgent, @Value("#{drishti['drishti.reporting.url']}") String url) {
        this.httpAgent = httpAgent;
        this.url = url;
    }

    @MotechListener(subjects = ReportEvent.SUBJECT)
    public void submitReportingData(MotechEvent event){
        String data = new Gson().toJson(event.getParameters().get("data"));
        HttpResponse response = httpAgent.post(url, data, "application/json");
        if (!response.isSuccess()) {
            logger.error("Reporting data post failed. URL: " + url + ". Data: " + data + ". Response: " + response.body());
        }
    }
}
