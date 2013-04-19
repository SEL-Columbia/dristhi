package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionRouter;
import org.ei.drishti.contract.*;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.service.ANCService;
import org.ei.drishti.service.DrishtiMCTSService;
import org.ei.drishti.service.ECService;
import org.ei.drishti.service.PNCService;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_DATA_KEY_NAME;

@Component
public class DrishtiController {
    private static Logger logger = LoggerFactory.getLogger(DrishtiController.class.toString());

    private final ANCService ancService;
    private final PNCService pncService;
    private DrishtiMCTSService mctsService;
    private ECService ecService;
    private ChildMapper childMapper;

    @Autowired
    public DrishtiController(CommCareFormSubmissionRouter router, ANCService ancService, PNCService pncService,
                             ECService ecService, DrishtiMCTSService drishtiMctsService, ChildMapper childMapper) {
        router.registerForDispatch(this);
        this.ancService = ancService;
        this.pncService = pncService;
        this.ecService = ecService;
        this.mctsService = drishtiMctsService;
        this.childMapper = childMapper;
    }

    public void registerEligibleCouple(EligibleCoupleRegistrationRequest eligibleCoupleRegistrationRequest, Map<String, Map<String, String>> extraData) {
        logger.info("Eligible couple registration: " + eligibleCoupleRegistrationRequest + ". Extra data: " + extraData);

        ecService.registerEligibleCouple(eligibleCoupleRegistrationRequest, extraData);
    }

    public void updateFamilyPlanningMethod(FamilyPlanningUpdateRequest familyPlanningUpdateRequest, Map<String, Map<String, String>> extraData) {
        logger.info("Eligible couple change FP: " + familyPlanningUpdateRequest + ". Extra data: " + extraData);

        ecService.updateFamilyPlanningMethod(familyPlanningUpdateRequest, extraData);
    }

    public void reportFPComplications(FPComplicationsRequest request, Map<String, Map<String, String>> extraData) {
        logger.info("Eligible couple report FP Complication: " + request + ". Extra data: " + extraData);

        ecService.reportFPComplications(request, extraData);
    }

    public void registerOutOfAreaANC(OutOfAreaANCRegistrationRequest request, Map<String, Map<String, String>> extraData) {
        logger.info("Register Out of Area ANC: " + request + ". Extra data: " + extraData);

        EligibleCouple couple = ecService.registerEligibleCoupleForOutOfAreaANC(request, extraData);
        ancService.registerOutOfAreaANC(request, couple, extraData);
    }

    public void registerMother(AnteNatalCareEnrollmentInformation enrollmentInformation, Map<String, Map<String, String>> extraData) {
        logger.info("Mother registration: " + enrollmentInformation + ". Extra data: " + extraData);

        ancService.registerANCCase(enrollmentInformation, extraData);
        mctsService.registerANCCase(enrollmentInformation);
    }

    public void updateANCCareInformation(AnteNatalCareInformation ancInformation, Map<String, Map<String, String>> extraData) {
        logger.info("ANC care: " + ancInformation + ". Extra data: " + extraData);

        ancService.ancHasBeenProvided(ancInformation, extraData);
        mctsService.ancCareHasBeenProvided(ancInformation);
    }

    public void updateSubsetOfANCInformation(AnteNatalCareInformationSubset request, Map<String, Map<String, String>> extraData) {
        logger.info("ANC subset updation: " + request + ". Extra data: " + extraData);

        ancService.updateSubsetOfANCInformation(request, extraData);
    }

    public void updateOutcomeOfANC(AnteNatalCareOutcomeInformation outcomeInformation, Map<String, Map<String, String>> extraData) {
        logger.info("ANC outcome: " + outcomeInformation + ". Extra data: " + extraData);

        ancService.updatePregnancyOutcome(outcomeInformation, extraData);
        mctsService.updateANCOutcome(outcomeInformation);

        List<ChildInformation> childInformationList = childMapper.mapDeliveryOutcomeInformationToChildren(outcomeInformation, extraData);

        for (ChildInformation childInformation : childInformationList) {
            pncService.registerChild(childInformation);
        }

        mctsService.registerChild(outcomeInformation);
    }

    public void updateChildImmunization(ChildImmunizationUpdationRequest request, Map<String, Map<String, String>> extraData) {
        logger.info("Child immunization updation: " + request + ". Extra data: " + extraData);

        pncService.updateChildImmunization(request, extraData);
        mctsService.updateChildImmunization(request);
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation, Map<String, Map<String, String>> extraData) {
        logger.info("ANC close: " + closeInformation + ". Extra data: " + extraData);

        ancService.closeANCCase(closeInformation, new SafeMap(extraData.get(REPORT_EXTRA_DATA_KEY_NAME)));
        mctsService.closeANCCase(closeInformation);
    }

    public void closePNCCase(PostNatalCareCloseInformation request, Map<String, Map<String, String>> extraData) {
        logger.info("PNC close: " + request + ". Extra data: " + extraData);

        pncService.closePNCCase(request, extraData);
        mctsService.closePNCCase(request);
    }

    public void closeChildCase(ChildCloseRequest childCloseRequest, Map<String, Map<String, String>> extraData) {
        logger.info("Child close: " + childCloseRequest);

        pncService.closeChildCase(childCloseRequest, extraData);
        mctsService.closeChildCase(childCloseRequest);
    }

    public void closeEligibleCouple(EligibleCoupleCloseRequest eligibleCoupleCloseRequest) {
        logger.info("Eligible couple close: " + eligibleCoupleCloseRequest);

        ecService.closeEligibleCouple(eligibleCoupleCloseRequest);
    }

    public void updateBirthPlanning(BirthPlanningRequest request, Map<String, Map<String, String>> extraData) {
        logger.info("Birth planning: " + request + ". Extra data: " + extraData);

        ancService.updateBirthPlanning(request, extraData);
    }

    public void updatePNCAndChildInformation(PostNatalCareInformation request, Map<String, Map<String, String>> extraData) {
        logger.info("PNC visit: " + request + ". Extra data: " + extraData);

        pncService.pncVisitHappened(request, extraData);
        mctsService.pncProvided(request);
    }
}
