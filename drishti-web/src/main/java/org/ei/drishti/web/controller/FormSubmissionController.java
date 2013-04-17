package org.ei.drishti.web.controller;

import ch.lambdaj.function.convert.Converter;
import org.ei.drishti.dto.form.FormSubmission;
import org.ei.drishti.event.FormSubmissionEvent;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;
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
    public ResponseEntity<HttpStatus> submitForms(@RequestBody List<FormSubmission> formSubmissions) {
        try {
            if (formSubmissions.isEmpty()) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

            List<org.ei.drishti.domain.form.FormSubmission> submissions = with(formSubmissions).convert(new Converter<FormSubmission, org.ei.drishti.domain.form.FormSubmission>() {
                @Override
                public org.ei.drishti.domain.form.FormSubmission convert(FormSubmission submission) {
                    return FormSubmissionConvertor.toFormSubmission(submission);
                }
            });
            gateway.sendEventMessage(new FormSubmissionEvent(submissions).toEvent());
            logger.info(format("Added Form submissions to queue.\nSubmissions: {0}", formSubmissions));
        } catch (Exception e) {
            logger.error(format("Form submissions processing failed with exception {0}.\nSubmissions: {1}", e, formSubmissions));
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
