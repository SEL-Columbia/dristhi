package org.ei.drishti.web.controller;

import org.ei.drishti.dto.FormSubmission;
import org.ei.drishti.event.FormSubmissionEvent;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/form-submissions")
public class FormSubmissionController {
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
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.CREATED;
    }
}
