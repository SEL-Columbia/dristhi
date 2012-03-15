package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionRouter;
import org.ei.drishti.contract.*;
import org.ei.drishti.service.ANCService;
import org.ei.drishti.service.DrishtiMCTSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class DrishtiController {
    private final ANCService ancService;
    private DrishtiMCTSService mctsService;
    private static Logger logger = Logger.getLogger(DrishtiController.class.toString());

    @Autowired
    public DrishtiController(CommCareFormSubmissionRouter router, ANCService ancService, DrishtiMCTSService drishtiMctsService) {
        router.registerForDispatch(this);
        this.ancService = ancService;
        this.mctsService = drishtiMctsService;
    }

    public void registerMother(AnteNatalCareEnrollmentInformation enrollmentInformation) {
        logger.info("Mother registration: " + enrollmentInformation);

        ancService.registerANCCase(enrollmentInformation);
        mctsService.registerANCCase(enrollmentInformation);
    }

    public void updateANCCareInformation(AnteNatalCareInformation ancInformation) {
        logger.info("ANC care: " + ancInformation);

        ancService.ancCareHasBeenProvided(ancInformation);
        mctsService.ancCareHasBeenProvided(ancInformation);
    }

    public void updateOutcomeOfANC(AnteNatalCareOutcomeInformation outcomeInformation) {
        logger.info("ANC outcome: " + outcomeInformation);

        ancService.updateANCOutcome(outcomeInformation);
        mctsService.updateANCOutcome(outcomeInformation);
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation) {
        logger.info("ANC close: " + closeInformation);

        ancService.closeANCCase(closeInformation);
        mctsService.closeANCCase(closeInformation);
    }

    public void registerChild(ChildRegistrationInformation childInformation) {
        logger.info("Child registration: " + childInformation);
    }
}
