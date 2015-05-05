package org.opensrp.web.controller.fake;

import org.opensrp.common.util.HttpAgent;
import org.opensrp.register.listener.ReportingEventListener;
import org.motechproject.scheduler.domain.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FakeReportController {
    private ReportingEventListener reportingEventListener;
    private HttpAgent httpAgent;
    private String reportURL;

    @Autowired
    public FakeReportController(ReportingEventListener reportingEventListener, HttpAgent httpAgent,
                                @Value("#{opensrp['drishti.reporting.url']}") String reportURL) {
        this.reportingEventListener = reportingEventListener;
        this.httpAgent = httpAgent;
        this.reportURL = reportURL;
    }

    @RequestMapping("/report/fetch")
    @ResponseBody
    public String runANMReportFetch() throws Exception {
        reportingEventListener.fetchANMReports(new MotechEvent("SUBJECT", null));
        return "Done";
    }

    @RequestMapping("/report/force-aggregate")
    @ResponseBody
    public String forceAggregateReports() throws Exception {
        httpAgent.get(reportURL + "/" + "force-aggregate");
        return "Done";
    }
}
