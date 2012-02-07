package org.ei.controller;

import org.ei.commcare.listener.event.CommCareFormSubmissionDispatcher;
import org.ei.contract.ChildRegistrationRequest;
import org.ei.contract.MotherRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DrishtiController {
    @Autowired
    public DrishtiController(CommCareFormSubmissionDispatcher dispatcher) {
        dispatcher.registerForDispatch(this);
    }

    public void registerMother(MotherRegistrationRequest request) {
        System.out.println("Mother registration: " + request);
    }

    public void registerChild(ChildRegistrationRequest request) {
        System.out.println("Child registration: " + request);
    }
}
