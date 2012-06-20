package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.reporting.repository.AllMotherRegistrations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReportDataController {
    private AllMotherRegistrations allMotherRegistrations;

    @Autowired
    public ReportDataController(AllMotherRegistrations allMotherRegistrations) {
        this.allMotherRegistrations = allMotherRegistrations;
    }

    @RequestMapping(value = "/report/submit", method = RequestMethod.POST)
    @ResponseBody
    public String submit(@RequestBody ReportingData reportingData) {
        if (reportingData.type().equals("updateChildImmunization")){

        }
        return "Success.";
    }
}
