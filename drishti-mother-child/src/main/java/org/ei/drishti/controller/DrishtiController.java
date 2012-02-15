package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionDispatcher;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.contract.ChildRegistrationInformation;
import org.ei.drishti.contract.MotherRegistrationInformation;
import org.ei.drishti.service.MotherService;
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
        System.out.println("Mother registration: " + motherInformation);
        motherService.enroll(motherInformation);
    }

    public void ancCare(AnteNatalCareInformation ancInformation) {
        System.out.println("ANC care: " + ancInformation);
        motherService.provideANCCare(ancInformation);
    }

    public void registerChild(ChildRegistrationInformation childInformation) {
        System.out.println("Child registration: " + childInformation);
    }
}
