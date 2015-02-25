package org.opensrp.reporting.controller;

import org.opensrp.reporting.service.AggregateReportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FakeReportDataController {
    private AggregateReportsService aggregateReportsService;
    private static final Logger logger = LoggerFactory.getLogger(FakeReportDataController.class);

    @Autowired
    public FakeReportDataController(AggregateReportsService aggregateReportsService) {
        this.aggregateReportsService = aggregateReportsService;
    }

    @RequestMapping(value = "/report/force-aggregate", method = RequestMethod.GET)
    @ResponseBody
    public String forceAggregateReports() {
        logger.info("Forcing report aggregation.");
        aggregateReportsService.sendReportsToAggregator();
        return "Done";
    }
}

