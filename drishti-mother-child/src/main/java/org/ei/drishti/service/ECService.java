package org.ei.drishti.service;

import org.ei.drishti.contract.EligibleCoupleCloseRequest;
import org.ei.drishti.contract.EligibleCoupleRegistrationRequest;
import org.ei.drishti.contract.OutOfAreaANCRegistrationRequest;
import org.ei.drishti.contract.UpdateDetailsRequest;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.reporting.ECReportingService;
import org.ei.drishti.service.scheduling.ECSchedulingService;
import org.ei.drishti.util.IdGenerator;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.CURRENT_FP_METHOD_CHANGE_DATE_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.CURRENT_FP_METHOD_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_MAPS_KEY_NAME;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION_MILESTONE;

@Service
public class ECService {
    private AllEligibleCouples allEligibleCouples;
    private ActionService actionService;
    private ECReportingService reportingService;
    private ECSchedulingService schedulingService;
    private IdGenerator idGenerator;
    private static Logger logger = LoggerFactory.getLogger(ActionService.class.toString());

    @Autowired
    public ECService(AllEligibleCouples allEligibleCouples, ActionService actionService, ECReportingService reportingService, IdGenerator idGenerator, ECSchedulingService schedulingService) {
        this.allEligibleCouples = allEligibleCouples;
        this.actionService = actionService;
        this.reportingService = reportingService;
        this.idGenerator = idGenerator;
        this.schedulingService = schedulingService;
    }

    public void registerEligibleCouple(EligibleCoupleRegistrationRequest request, Map<String, Map<String, String>> extraData) {
        EligibleCouple couple = new EligibleCouple(request.caseId(), request.ecNumber())
                .withCouple(request.wife(), request.husband()).withANMIdentifier(request.anmIdentifier())
                .withLocation(request.village(), request.subCenter(), request.phc()).withDetails(extraData.get("details"));

        allEligibleCouples.register(couple);

        reportingService.fpMethodChangedWithECRegistrationDetails(new SafeMap(extraData.get(REPORT_EXTRA_MAPS_KEY_NAME)), couple.village(), couple.subCenter(), couple.phc());
        actionService.registerEligibleCouple(request.caseId(), request.ecNumber(), request.wife(), request.husband(),
                request.anmIdentifier(), request.village(), request.subCenter(), request.phc(), extraData.get("details"));
        schedulingService.enrollToFPComplications(request);
    }

    public EligibleCouple registerEligibleCoupleForOutOfAreaANC(OutOfAreaANCRegistrationRequest request, Map<String, Map<String, String>> extraData) {
        EligibleCouple couple = new EligibleCouple(idGenerator.generateUUID().toString(), "0")
                .withCouple(request.wife(), request.husband()).withANMIdentifier(request.anmIdentifier())
                .withLocation(request.village(), request.subCenter(), request.phc()).withDetails(extraData.get("details"))
                .asOutOfArea();

        allEligibleCouples.register(couple);
        return couple;
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
        reportingService.fpMethodChangedWithUpdatedECDetails(new SafeMap(extraDetails.get(REPORT_EXTRA_MAPS_KEY_NAME)), updatedCouple.ecNumber(), updatedCouple.village(), updatedCouple.subCenter(), updatedCouple.phc());
        actionService.updateEligibleCoupleDetails(request.caseId(), request.anmIdentifier(), updatedCouple.details());

        schedulingService.updateFPComplications(request.caseId(), extraDetails.get("details"));

        closeAlertsForFPComplications(request, extraDetails.get("details"));
    }

    private void closeAlertsForFPComplications(UpdateDetailsRequest request, Map<String,String> details) {
        if(!(details.get(CURRENT_FP_METHOD_COMMCARE_FIELD_NAME).equalsIgnoreCase("none") || details.get(CURRENT_FP_METHOD_COMMCARE_FIELD_NAME).isEmpty())){
            actionService.markAlertAsClosed(request.caseId(), request.anmIdentifier(), EC_SCHEDULE_FP_COMPLICATION_MILESTONE, details.get(CURRENT_FP_METHOD_CHANGE_DATE_COMMCARE_FIELD_NAME));
        }
    }
}
