package org.ei.drishti.web.controller;

import ch.lambdaj.function.convert.Converter;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.event.FormSubmissionEvent;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.service.FormSubmissionConvertor;
import org.ei.drishti.form.service.SubmissionService;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.text.MessageFormat.format;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/form-submissions")
public class FormSubmissionController {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionController.class.toString());
    private SubmissionService formSubmissionService;
    private OutboundEventGateway gateway;

    @Autowired
    public FormSubmissionController(SubmissionService submissionService, OutboundEventGateway gateway) {
        this.formSubmissionService = submissionService;
        this.gateway = gateway;
    }

    @RequestMapping(method = GET)
    @ResponseBody
    private List<FormSubmissionDTO> getNewSubmissionsForANM(@RequestParam("anm-id") String anmIdentifier, @RequestParam("timestamp") Long timeStamp) {
        List<FormSubmission> newSubmissionsForANM = formSubmissionService.getNewSubmissionsForANM(anmIdentifier, timeStamp);
        return with(newSubmissionsForANM).convert(new Converter<FormSubmission, FormSubmissionDTO>() {
            @Override
            public FormSubmissionDTO convert(FormSubmission submission) {
                return FormSubmissionConvertor.from(submission);
            }
        });
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = POST)
    public ResponseEntity<HttpStatus> submitForms(@RequestBody List<FormSubmissionDTO> formSubmissionsDTO) {
        try {
            if (formSubmissionsDTO.isEmpty()) {
                return new ResponseEntity<>(BAD_REQUEST);
            }

            gateway.sendEventMessage(new FormSubmissionEvent(formSubmissionsDTO).toEvent());
            logger.debug(format("Added Form submissions to queue.\nSubmissions: {0}", formSubmissionsDTO));
        } catch (Exception e) {
            logger.error(format("Form submissions processing failed with exception {0}.\nSubmissions: {1}", e, formSubmissionsDTO));
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(CREATED);
    }
}
