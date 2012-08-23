package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionRouter;
import org.ei.drishti.contract.*;
import org.ei.drishti.service.ANCService;
import org.ei.drishti.service.DrishtiMCTSService;
import org.ei.drishti.service.ECService;
import org.ei.drishti.service.PNCService;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DrishtiController {
    private static Logger logger = LoggerFactory.getLogger(DrishtiController.class.toString());

    private final ANCService ancService;
    private final PNCService pncService;
    private DrishtiMCTSService mctsService;
    private ECService ecService;

    @Autowired
    public DrishtiController(CommCareFormSubmissionRouter router, ANCService ancService, PNCService pncService, ECService ecService, DrishtiMCTSService drishtiMctsService) {
        router.registerForDispatch(this);
        this.ancService = ancService;
        this.pncService = pncService;
        this.ecService = ecService;
        this.mctsService = drishtiMctsService;
    }

    public void registerMother(AnteNatalCareEnrollmentInformation enrollmentInformation, Map<String, Map<String, String>> extraData) {
        logger.info("Mother registration: " + enrollmentInformation + ". Extra data: " + extraData);

        ancService.registerANCCase(enrollmentInformation, new SafeMap(extraData.get("reporting")));
        mctsService.registerANCCase(enrollmentInformation);
    }

    public void updateANCCareInformation(AnteNatalCareInformation ancInformation) {
        logger.info("ANC care: " + ancInformation);

        ancService.ancCareHasBeenProvided(ancInformation);
        mctsService.ancCareHasBeenProvided(ancInformation);
    }

    public void updateOutcomeOfANC(AnteNatalCareOutcomeInformation outcomeInformation) {
        logger.info("ANC outcome: " + outcomeInformation);

        ancService.updatePregnancyOutcome(outcomeInformation);
        mctsService.updateANCOutcome(outcomeInformation);
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation, Map<String, Map<String, String>> extraData) {
        logger.info("ANC close: " + closeInformation + ". Extra data: " + extraData);

        ancService.closeANCCase(closeInformation, new SafeMap(extraData.get("reporting")));
        mctsService.closeANCCase(closeInformation);
    }

    public void registerChild(ChildRegistrationRequest childInformation) {
        logger.info("New child registration: " + childInformation);

        pncService.registerChild(childInformation);
        mctsService.registerChild(childInformation);
    }

    public void updateChildImmunization(ChildImmunizationUpdationRequest updationRequest, Map<String, Map<String, String>> extraData) {
        logger.info("Child immunization updation: " + updationRequest + ". Extra data: " + extraData);

        pncService.updateChildImmunization(updationRequest, new SafeMap(extraData.get("reporting")));
        mctsService.updateChildImmunization(updationRequest);
    }

    public void closeChildCase(ChildCloseRequest childCloseRequest) {
        logger.info("Child close: " + childCloseRequest);

        pncService.closeChildCase(childCloseRequest);
        mctsService.closeChildCase(childCloseRequest);
    }

    public void registerEligibleCouple(EligibleCoupleRegistrationRequest eligibleCoupleRegistrationRequest, Map<String, Map<String, String>> extraData) {
        logger.info("Eligible couple registration: " + eligibleCoupleRegistrationRequest + ". Extra data: " + extraData);

        ecService.registerEligibleCouple(eligibleCoupleRegistrationRequest, extraData);
    }

    public void closeEligibleCouple(EligibleCoupleCloseRequest eligibleCoupleCloseRequest) {
        logger.info("Eligible couple close: " + eligibleCoupleCloseRequest);

        ecService.closeEligibleCouple(eligibleCoupleCloseRequest);
    }
}
