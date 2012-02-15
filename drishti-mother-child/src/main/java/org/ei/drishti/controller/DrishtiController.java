package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionDispatcher;
import org.ei.drishti.contract.ANCRequest;
import org.ei.drishti.contract.ChildRegistrationRequest;
import org.ei.drishti.contract.MotherRegistrationInformation;
import org.ei.drishti.service.MotherService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DrishtiController {
    private final MotherService motherService;

    @Autowired
    public DrishtiController(CommCareFormSubmissionDispatcher dispatcher, MotherService motherService) {
        this.motherService = motherService;
        dispatcher.registerForDispatch(this);
    }

    public void registerMother(MotherRegistrationInformation motherInformation) {
        motherService.enroll(motherInformation);
        System.out.println("Mother registered: " + motherInformation + ". Time now is: " + DateTime.now());
    }

    public void registerChild(ChildRegistrationRequest request) {
        System.out.println("Child registration: " + request);
    }

    public void ancCare(ANCRequest request) {
        System.out.println("ANC register: " + request);
    }
}
