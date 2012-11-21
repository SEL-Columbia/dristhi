package org.ei.drishti.web.controller;

import org.ei.drishti.listener.ReportingEventListener;
import org.motechproject.scheduler.domain.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FakeReportController {
    private ReportingEventListener reportingEventListener;

    @Autowired
    public FakeReportController(ReportingEventListener reportingEventListener) {
        this.reportingEventListener = reportingEventListener;
    }

    @RequestMapping("/report/fetch")
    @ResponseBody
    public String runANMReportFetch() throws Exception {
        reportingEventListener.fetchANMReports(new MotechEvent("SUBJECT", null));
        return "Done";
    }
}
