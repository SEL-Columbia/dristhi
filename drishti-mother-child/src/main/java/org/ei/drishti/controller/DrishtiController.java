package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionRouter;
import org.ei.drishti.contract.*;
import org.ei.drishti.service.ANCService;
import org.ei.drishti.service.DrishtiMCTSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ei.drishti.service.PNCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DrishtiController {
    private static Logger logger = LoggerFactory.getLogger(DrishtiController.class.toString());

    private final ANCService ancService;
    private final PNCService pncService;
    private DrishtiMCTSService mctsService;

    @Autowired
    public DrishtiController(CommCareFormSubmissionRouter router, ANCService ancService, PNCService pncService, DrishtiMCTSService drishtiMctsService) {
        router.registerForDispatch(this);
        this.ancService = ancService;
        this.pncService = pncService;
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

        pncService.registerChild(childInformation);
        mctsService.registerChild(childInformation);
    }

    public void registerNewChild(ChildRegistrationRequest childInformation) {
        logger.info("New Child registration: " + childInformation);

        pncService.registerNewChild(childInformation);
        mctsService.registerNewChild(childInformation);
    }
}
