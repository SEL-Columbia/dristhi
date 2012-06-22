package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.reporting.repository.AllServicesProvided;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReportDataController {
    private AllServicesProvided allServicesProvided;

    @Autowired
    public ReportDataController(AllServicesProvided allServicesProvided) {
        this.allServicesProvided = allServicesProvided;
    }

    @RequestMapping(value = "/report/submit", method = RequestMethod.POST)
    @ResponseBody
    public String submit(@RequestBody ReportingData reportingData) {
        if (reportingData.type().equals("updateChildImmunization")) {
            allServicesProvided.save(reportingData.get("thaayiCardNumber"), LocalDate.parse(reportingData.get("immunizationsProvidedDate")).toDate(),
                    reportingData.get("immunizationsProvided"), reportingData.get("anmIdentifier"));
        }
        return "Success.";
    }
}
