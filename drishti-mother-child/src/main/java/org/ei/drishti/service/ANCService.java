package org.ei.drishti.service;

import org.ei.drishti.contract.*;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.reporting.MotherReportingService;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.text.MessageFormat.format;
import static org.joda.time.LocalTime.now;

@Service
public class ANCService {
    private static Logger logger = LoggerFactory.getLogger(ANCService.class.toString());

    private final AllMothers allMothers;
    private AllEligibleCouples eligibleCouples;
    private ANCSchedulesService ancSchedulesService;
    private ActionService actionService;
    private MotherReportingService reportingService;

    @Autowired
    public ANCService(AllMothers allMothers, AllEligibleCouples eligibleCouples, ANCSchedulesService ancSchedulesService, ActionService actionService, MotherReportingService reportingService) {
        this.allMothers = allMothers;
        this.eligibleCouples = eligibleCouples;
        this.ancSchedulesService = ancSchedulesService;
        this.actionService = actionService;
        this.reportingService = reportingService;
    }

    public void registerANCCase(AnteNatalCareEnrollmentInformation info, Map<String, Map<String, String>> extraData) {
        Map<String, String> details = extraData.get("details");

        EligibleCouple couple = eligibleCouples.findByCaseId(info.ecCaseId());
        if (couple == null) {
            logger.warn(format("Found pregnancy without registered eligible couple. Ignoring case: {0} for mother with case: {1} for ANM: {2}",
                    info.ecCaseId(), info.caseId(), info.anmIdentifier()));
            return;
        }

        Mother mother = new Mother(info.caseId(), info.thaayiCardNumber(), couple.wife()).withAnm(info.anmIdentifier(), info.anmPhoneNumber())
                .withLMP(info.lmpDate()).withLocation(couple.village(), couple.subCenter(), couple.phc())
                .withDetails(details);
        allMothers.register(mother);
        actionService.registerPregnancy(info.caseId(), couple.caseId(), info.thaayiCardNumber(), info.anmIdentifier(), info.lmpDate(), details);
        reportingService.registerANC(new SafeMap(extraData.get("reporting")), couple.village(), couple.subCenter());

        enrollMotherIntoSchedules(info.caseId(), info.lmpDate());
    }

    private void enrollMotherIntoSchedules(String caseId, LocalDate lmpDate) {
        Time preferredAlertTime = new Time(new LocalTime(14, 0));
        LocalDate referenceDate = lmpDate != null ? lmpDate : DateUtil.today();

        ancSchedulesService.enrollMother(caseId, referenceDate, new Time(now()), preferredAlertTime);
    }

    public void registerOutOfAreaANC(OutOfAreaANCRegistrationRequest request, EligibleCouple couple, Map<String, Map<String, String>> extraData) {
        Map<String, String> details = extraData.get("details");

        Mother mother = new Mother(request.caseId(), request.thaayiCardNumber(), request.wife()).withAnm(request.anmIdentifier(), request.anmPhoneNumber())
                .withLMP(request.lmpDate()).withLocation(request.village(), request.subCenter(), request.phc())
                .withDetails(details);

        allMothers.register(mother);
        actionService.registerOutOfAreaANC(request.caseId(), couple.caseId(), request.wife(), request.husband(), request.anmIdentifier(), request.village(), request.subCenter(), request.phc(), request.thaayiCardNumber(),
                request.lmpDate(), details);
        enrollMotherIntoSchedules(request.caseId(), request.lmpDate());
    }

    public void ancCareHasBeenProvided(AnteNatalCareInformation ancInformation, Map<String, Map<String, String>> extraData) {
        if (!allMothers.motherExists(ancInformation.caseId())) {
            logger.warn("Found care provided without registered mother for case ID: " + ancInformation.caseId());
            return;
        }

        if (ancInformation.visitNumber() > 0) {
            ancSchedulesService.ancVisitHasHappened(ancInformation);
        }

        if (ancInformation.ifaTablesHaveBeenProvided()) {
            ancSchedulesService.ifaVisitHasHappened(ancInformation);
        }
        if (ancInformation.wasTTShotProvided()) {
            ancSchedulesService.ttVisitHasHappened(ancInformation);
        }

        Mother motherWithUpdatedDetails = allMothers.updateDetails(ancInformation.caseId(), extraData.get("details"));
        actionService.updateMotherDetails(motherWithUpdatedDetails.caseId(), motherWithUpdatedDetails.anmIdentifier(), motherWithUpdatedDetails.details());
        actionService.ancCareProvided(motherWithUpdatedDetails.caseId(), motherWithUpdatedDetails.anmIdentifier(), ancInformation.visitNumber(), ancInformation.visitDate(), ancInformation.numberOfIFATabletsProvided(), ancInformation.wasTTShotProvided(), ancInformation.ttDose());
    }

    public void updatePregnancyOutcome(AnteNatalCareOutcomeInformation outcomeInformation, Map<String, Map<String, String>> extraData) {
        String caseId = outcomeInformation.caseId();
        if (!allMothers.motherExists(caseId)) {
            logger.warn("Failed to update delivery outcome as there is no mother registered with case ID: " + caseId);
            return;
        }
        ancSchedulesService.unEnrollFromSchedules(caseId);
        actionService.updateANCOutcome(caseId, outcomeInformation.anmIdentifier(), extraData.get("details"));
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation, SafeMap data) {
        if (!allMothers.motherExists(closeInformation.caseId())) {
            logger.warn("Tried to close case without registered mother for case ID: " + closeInformation.caseId());
            return;
        }

        reportingService.closeANC(data);
        ancSchedulesService.unEnrollFromSchedules(closeInformation.caseId());
        actionService.closeANC(closeInformation.caseId(), closeInformation.anmIdentifier(), closeInformation.reason());
    }
}
