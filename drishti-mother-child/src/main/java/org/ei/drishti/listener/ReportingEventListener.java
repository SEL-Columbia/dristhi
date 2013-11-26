package org.ei.drishti.listener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ei.drishti.common.domain.ANMReport;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.event.ReportEvent;
import org.ei.drishti.event.ReportUpdateEvent;
import org.ei.drishti.service.reporting.ANMReportingService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.ei.drishti.scheduler.ANMReportScheduler.SUBJECT;

@Component
public class ReportingEventListener {
    public static final String FETCH_REPORTS_FOR_ALL_ANMS_ACTION = "fetchForAllANMs";
    public static final String SUBMIT_REPORT_ACTION = "submit";
    public static final String UPDATE_REPORT_ACTION = "update";
    private ANMReportingService anmReportService;
    private HttpAgent httpAgent;
    private String url;
    private static Logger logger = LoggerFactory.getLogger(ReportingEventListener.class);

    @Autowired
    public ReportingEventListener(ANMReportingService anmReportService, HttpAgent httpAgent,
                                  @Value("#{drishti['drishti.reporting.url']}") String url) {
        this.anmReportService = anmReportService;
        this.httpAgent = httpAgent;
        this.url = url;
    }

    @MotechListener(subjects = ReportEvent.SUBJECT)
    public void submitReportingData(MotechEvent event) {
        String data = new Gson().toJson(event.getParameters().get("data"));
        HttpResponse response = httpAgent.post(url + "/" + SUBMIT_REPORT_ACTION, data, MediaType.APPLICATION_JSON_VALUE);
        if (!response.isSuccess()) {
            logger.error("Reporting data post failed. URL: " + url + "/" + SUBMIT_REPORT_ACTION +". Data: " + data + ". Response: " + response.body());
        }
    }

    @MotechListener(subjects = ReportUpdateEvent.SUBJECT)
    public void updateReportingData(MotechEvent event) {
        String data = new Gson().toJson(event.getParameters().get("data"));
        HttpResponse response = httpAgent.post(url + "/" + UPDATE_REPORT_ACTION, data, MediaType.APPLICATION_JSON_VALUE);
        if (!response.isSuccess()) {
            logger.error("Reporting data post failed. URL: " + url + "/" + UPDATE_REPORT_ACTION + ". Data: " + data + ". Response: " + response.body());
        }
    }

    @MotechListener(subjects = SUBJECT)
    public void fetchANMReports(MotechEvent event) throws Exception {
        logger.info("Fetching ANM reports...");

        anmReportService.reportFromEntityData();
        HttpResponse response = httpAgent.get(url + "/" + FETCH_REPORTS_FOR_ALL_ANMS_ACTION);
        if (!response.isSuccess()) {
            logger.error("ANM Reports fetch failed. URL: " + url + "/" + FETCH_REPORTS_FOR_ALL_ANMS_ACTION +". Response body: " + response.body());
            return;
        }
        List<ANMReport> anmReports = new Gson().fromJson(response.body(), new TypeToken<List<ANMReport>>() {
        }.getType());
        if (anmReports.isEmpty()) {
            logger.info("No ANM Reports fetched");
            return;
        }
        anmReportService.processReports(anmReports);

        logger.info("Done fetching ANM reports.");
    }
}
