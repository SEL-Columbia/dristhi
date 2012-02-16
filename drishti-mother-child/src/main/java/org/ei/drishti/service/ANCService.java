package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareCloseInformation;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.contract.AnteNatalCareEnrollmentInformation;
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

@Service
public class ANCService {
    public static final String SCHEDULE_NAME = "Ante Natal Care - Normal";
    private final AllMothers allMothers;
    private ScheduleTrackingService trackingService;

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
        trackingService.fulfillCurrentMilestone(ancInformation.caseId(), SCHEDULE_NAME);
    }

    public void updateANCOutcome(AnteNatalCareOutcomeInformation outcomeInformation) {
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation) {
        trackingService.unenroll(closeInformation.caseId(), SCHEDULE_NAME);
    }
}
