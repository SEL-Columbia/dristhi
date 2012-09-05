package org.ei.drishti.service;

import org.ei.drishti.contract.EligibleCoupleCloseRequest;
import org.ei.drishti.contract.OutOfAreaANCRegistrationRequest;
import org.ei.drishti.contract.UpdateDetailsRequest;
import org.ei.drishti.contract.EligibleCoupleRegistrationRequest;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ECService {
    private AllEligibleCouples allEligibleCouples;
    private ActionService actionService;
    private static Logger logger = LoggerFactory.getLogger(ActionService.class.toString());

    @Autowired
    public ECService(AllEligibleCouples allEligibleCouples, ActionService actionService) {
        this.allEligibleCouples = allEligibleCouples;
        this.actionService = actionService;
    }

    public void registerEligibleCouple(EligibleCoupleRegistrationRequest request, Map<String, Map<String, String>> extraData) {
        EligibleCouple couple = new EligibleCouple(request.caseId(), request.ecNumber())
                .withCouple(request.wife(), request.husband()).withANMIdentifier(request.anmIdentifier())
                .withLocation(request.village(), request.subCenter(), request.phc()).withDetails(extraData.get("details"));

        allEligibleCouples.register(couple);

        actionService.registerEligibleCouple(request.caseId(), request.ecNumber(), request.wife(), request.husband(),
                request.anmIdentifier(), request.village(), request.subCenter(), request.phc(), extraData.get("details"));
    }

    public void registerEligibleCoupleForOutOfAreaANC(OutOfAreaANCRegistrationRequest request, Map<String, Map<String, String>> extraData) {
        EligibleCouple couple = new EligibleCouple(request.caseId(), "0")
                .withCouple(request.wife(), request.husband()).withANMIdentifier(request.anmIdentifier())
                .withLocation(request.village(), request.subCenter(), request.phc()).withDetails(extraData.get("details"))
                .asOutOfArea();

        allEligibleCouples.register(couple);
    }

    public void closeEligibleCouple(EligibleCoupleCloseRequest request) {
        allEligibleCouples.close(request.caseId());
        actionService.closeEligibleCouple(request.caseId(), request.anmIdentifier());
    }

    public void updateDetails(UpdateDetailsRequest request, Map<String, Map<String, String>> extraDetails) {
        EligibleCouple couple = allEligibleCouples.findByCaseId(request.caseId());
        if (couple == null) {
            logger.warn("Tried to update details of a non-existing EC: " + request + ". Extra details: " + extraDetails);
            return;
        }

        EligibleCouple updatedCouple = allEligibleCouples.updateDetails(request.caseId(), extraDetails.get("details"));
        actionService.updateEligibleCoupleDetails(request.caseId(), request.anmIdentifier(), updatedCouple.details());
    }
}
