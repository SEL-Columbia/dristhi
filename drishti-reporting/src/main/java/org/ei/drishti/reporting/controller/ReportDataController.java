package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ReportingData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReportDataController {
    @RequestMapping(value = "/report/submit", method = RequestMethod.POST)
    @ResponseBody
    public String submit(@RequestBody ReportingData reportingData) {
        System.out.println("Data: " + reportingData);
        return "Success.";
    }
}
