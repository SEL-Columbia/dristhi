package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionDispatcher;
import org.ei.drishti.contract.*;
import org.ei.drishti.service.ANCService;
import org.ei.drishti.service.MCTSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DrishtiController {
    private final ANCService ancService;
    private MCTSService mctsService;

    @Autowired
    public DrishtiController(CommCareFormSubmissionDispatcher dispatcher, ANCService ancService, MCTSService mctsService) {
        dispatcher.registerForDispatch(this);
        this.ancService = ancService;
        this.mctsService = mctsService;
    }

    public void registerMother(AnteNatalCareEnrollmentInformation enrollmentInformation) {
        System.out.println("Mother registration: " + enrollmentInformation);
        ancService.registerANCCase(enrollmentInformation);
        mctsService.registerANCCase(enrollmentInformation);
    }

    public void updateANCCareInformation(AnteNatalCareInformation ancInformation) {
        System.out.println("ANC care: " + ancInformation);
        ancService.ancCareHasBeenProvided(ancInformation);
        mctsService.ancCareHasBeenProvided(ancInformation);
    }

    public void updateOutcomeOfANC(AnteNatalCareOutcomeInformation outcomeInformation) {
        System.out.println("ANC outcome: " + outcomeInformation);
        ancService.updateANCOutcome(outcomeInformation);
        mctsService.updateANCOutcome(outcomeInformation);
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation) {
        System.out.println("ANC close: " + closeInformation);
        ancService.closeANCCase(closeInformation);
        mctsService.closeANCCase(closeInformation);
    }

    public void registerChild(ChildRegistrationInformation childInformation) {
        System.out.println("Child registration: " + childInformation);
    }
}
