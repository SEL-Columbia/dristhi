package org.ei.drishti.service;

import org.ei.drishti.contract.AnteNatalCareCloseInformation;
import org.ei.drishti.contract.AnteNatalCareInformation;
import org.ei.drishti.contract.AnteNatalCareOutcomeInformation;
import org.ei.drishti.contract.AnteNatalCareEnrollmentInformation;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.DateTime;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ANCService {
    private final AllMothers allMothers;
    private ScheduleTrackingService trackingService;

    @Autowired
    public ANCService(AllMothers allMothers, ScheduleTrackingService trackingService) {
        this.allMothers = allMothers;
        this.trackingService = trackingService;
    }

    public void registerANCCase(AnteNatalCareEnrollmentInformation info) {
        Mother mother = new Mother(info.caseId(), info.thaayiCardNumber(), info.name());
        allMothers.register(mother);

        DateTime now = DateUtil.now();
        Time preferredAlertTime = new Time(now.hourOfDay().get(), now.minuteOfHour().get() + 2);
        trackingService.enroll(new EnrollmentRequest(info.caseId(), "IPTI Schedule", preferredAlertTime, DateUtil.today()));
    }

    public void updateANCCareInformation(AnteNatalCareInformation ancInformation) {
    }

    public void updateANCOutcome(AnteNatalCareOutcomeInformation outcomeInformation) {
    }

    public void closeANCCase(AnteNatalCareCloseInformation closeInformation) {
    }
}
