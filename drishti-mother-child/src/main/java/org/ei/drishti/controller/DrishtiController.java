package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionRouter;
import org.ei.drishti.contract.BirthPlanningRequest;
import org.ei.drishti.service.ANCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DrishtiController {
    private static Logger logger = LoggerFactory.getLogger(DrishtiController.class.toString());
    private final ANCService ancService;

    @Autowired
    public DrishtiController(CommCareFormSubmissionRouter router, ANCService ancService) {
        router.registerForDispatch(this);
        this.ancService = ancService;
    }

    public void updateBirthPlanning(BirthPlanningRequest request, Map<String, Map<String, String>> extraData) {
        logger.info("Birth planning: " + request + ". Extra data: " + extraData);

        ancService.updateBirthPlanning(request, extraData);
    }
}
