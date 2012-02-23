package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareCloseInformation;
import org.ei.drishti.contract.AnteNatalCareEnrollmentInformation;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class ANCService {
    public static final String SCHEDULE_NAME = "Ante Natal Care - Normal";
    private final AllMothers allMothers;
    private ScheduleTrackingService trackingService;
    private static Logger logger = Logger.getLogger(ANCService.class.toString());

    @Autowired
    public ANCService(AllMothers allMothers, ScheduleTrackingService trackingService) {
        this.allMothers = allMothers;
        this.trackingService = trackingService;
    }

    public void registerANCCase(AnteNatalCareEnrollmentInformation info) {
        Mother mother = new Mother(info.caseId(), info.thaayiCardNumber(), info.name()).withAnmPhoneNumber(info.anmPhoneNumber()).withLMP(info.lmpDate());
        allMothers.register(mother);

        DateTime now = DateUtil.now();
        Time preferredAlertTime = new Time(now.hourOfDay().get(), now.minuteOfHour().get() + 2);
        LocalDate referenceDate = info.lmpDate() != null ? info.lmpDate() : DateUtil.today();

        trackingService.enroll(new EnrollmentRequest(info.caseId(), SCHEDULE_NAME, preferredAlertTime, referenceDate));
    }

    public void ancCareHasBeenProvided(AnteNatalCareInformation ancInformation) {
        if (!allMothers.motherExists(ancInformation.caseId())) {
            logger.warning("Found care provided without registered mother for case ID: " + ancInformation.caseId());
            return;
        }
        trackingService.fulfillCurrentMilestone(ancInformation.caseId(), SCHEDULE_NAME);
    }

    public void updateANCOutcome(AnteNatalCareOutcomeInformation outcomeInformation) {
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation) {
        if (!allMothers.motherExists(closeInformation.caseId())) {
            logger.warning("Tried to close case without registered mother for case ID: " + closeInformation.caseId());
            return;
        }
        trackingService.unenroll(closeInformation.caseId(), SCHEDULE_NAME);
    }
}
