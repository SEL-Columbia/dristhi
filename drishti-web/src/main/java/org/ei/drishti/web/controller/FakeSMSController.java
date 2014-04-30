package org.ei.drishti.web.controller;

import org.ei.drishti.service.DrishtiSMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FakeSMSController {

    private DrishtiSMSService drishtiSMSService;
    private static Logger logger = LoggerFactory.getLogger(FakeSMSController.class.toString());

    @Autowired
    public FakeSMSController(DrishtiSMSService drishtiSMSService) {
        this.drishtiSMSService = drishtiSMSService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/send-sms")
    @Transactional(value = "smsTransaction")
    @ResponseBody
    public String sendSMS() {
        logger.debug("SMS Will be sent now");
        drishtiSMSService.sendSMS("9663367421", "Test SMS");
        return "Success";
    }
}
