package org.ei.drishti.web.controller;

import org.ei.drishti.dto.form.FormSubmission;
import org.ei.drishti.event.FormSubmissionEvent;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static java.text.MessageFormat.format;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/form-submissions")
public class FormSubmissionController {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionController.class.toString());
    private OutboundEventGateway gateway;

    @Autowired
    public FormSubmissionController(OutboundEventGateway gateway) {
        this.gateway = gateway;
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = POST)
    @ResponseBody
    public HttpStatus submitForms(@RequestBody List<FormSubmission> formSubmissions) {
        try {
            gateway.sendEventMessage(new FormSubmissionEvent(formSubmissions).toEvent());
            logger.info(format("Added Form submissions to queue.\nSubmissions: {0}", formSubmissions));
        } catch (Exception e) {
            logger.error(format("Form submissions processing failed with exception {0}.\nSubmissions: {1}", e, formSubmissions));
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.CREATED;
    }
}
