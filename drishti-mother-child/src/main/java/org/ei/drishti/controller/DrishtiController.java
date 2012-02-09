package org.ei.drishti.controller;

import org.ei.commcare.listener.event.CommCareFormSubmissionDispatcher;
import org.ei.drishti.contract.ChildRegistrationRequest;
import org.ei.drishti.contract.MotherRegistrationRequest;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DrishtiController {
    private ScheduleTrackingService trackingService;

    @Autowired
    public DrishtiController(CommCareFormSubmissionDispatcher dispatcher, ScheduleTrackingService trackingService) {
        this.trackingService = trackingService;
        dispatcher.registerForDispatch(this);
    }

    public void registerMother(MotherRegistrationRequest request) {
        Time preferredAlertTime = new Time(DateUtil.now().hourOfDay().get(), DateUtil.now().minuteOfHour().get() + 2);
        trackingService.enroll(new EnrollmentRequest(request.thaayiCardNumber(), "IPTI Schedule", preferredAlertTime, DateUtil.today()));

        System.out.println("Mother registration: " + request + " set to: " + preferredAlertTime);
    }

    public void registerChild(ChildRegistrationRequest request) {
        System.out.println("Child registration: " + request);
    }
}
