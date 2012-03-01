package org.ei.drishti.controller.util;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.quartz.*;
import org.quartz.impl.calendar.BaseCalendar;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.*;

import static java.text.MessageFormat.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.scheduletracking.api.events.constants.EventDataKeys.*;

public class FakeSchedule {
    private ScheduleTrackingService trackingService;
    private final SetDateAction setDateAction;
    private final Scheduler scheduler;
    private Map<Pair, List<Date>> alertTimes;
    private final LinkedList<Date> fulfillmentDates;
    private String name;

    public FakeSchedule(ScheduleTrackingService trackingService, SchedulerFactoryBean schedulerFactoryBean, SetDateAction setDateAction) {
        this.trackingService = trackingService;
        this.scheduler = schedulerFactoryBean.getScheduler();
        this.setDateAction = setDateAction;

        this.alertTimes = new HashMap<Pair, List<Date>>();
        fulfillmentDates = new LinkedList<Date>();
    }

    public void enrollFor(String scheduleName, LocalDate referenceDateForEnrollment, Time preferredAlertTime) throws Exception {
        String externalId = String.valueOf(new Random().nextFloat());
        this.name = scheduleName;

        setDateAction.setTheDateTo(referenceDateForEnrollment);
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(externalId, scheduleName, preferredAlertTime, referenceDateForEnrollment, null, null);
        trackingService.enroll(enrollmentRequest);

        while (true) {
            captureAlertsFor(externalId, scheduleName);
            try {
                LocalDate fulfillmentDate = nextFulfillmentDate();
                setDateAction.setTheDateTo(fulfillmentDate);
                trackingService.fulfillCurrentMilestone(externalId, scheduleName, fulfillmentDate);
            } catch (InvalidEnrollmentException e) {
                break;
            }
        }
    }

    public FakeSchedule withFulfillmentDates(Date... fulfillmentDates) {
        this.fulfillmentDates.addAll(Arrays.asList(fulfillmentDates));
        return this;
    }

    public void assertAlertsStartWith(String milestoneName, WindowName window, Date... expectedTimes) {
        assertAlertTimes(milestoneName, window, expectedTimes, true);
    }

    public void assertAlerts(String milestoneName, WindowName window, Date... expectedTimes) {
        assertAlertTimes(milestoneName, window, expectedTimes, false);
    }

    private void assertAlertTimes(String milestoneName, WindowName window, Date[] expectedTimes, boolean shouldDoPartialCheck) {
        List<Comparable> sortableActualAlertTimes = new ArrayList<Comparable>(getTriggerTimesFor(milestoneName, window.name()));
        List<Comparable> sortableExpectedAlertTimes = new ArrayList<Comparable>(Arrays.asList(expectedTimes));

        Collections.sort(sortableActualAlertTimes);
        Collections.sort(sortableExpectedAlertTimes);

        if (shouldDoPartialCheck && sortableActualAlertTimes.size() > expectedTimes.length) {
            sortableActualAlertTimes = sortableActualAlertTimes.subList(0, expectedTimes.length);
        }

        assertThat(format("{0} alerts for {1} window did not match.", milestoneName, window),
                sortableActualAlertTimes, is(sortableExpectedAlertTimes));
    }

    public void assertNoAlerts(String milestoneName, WindowName window) {
        assertThat(format("Expected no alerts for {0} window of milestone {1}", window, milestoneName),
                getTriggerTimesFor(milestoneName, window.name()), is(Collections.<Date>emptyList()));
    }

    private void captureAlertsFor(String externalId, String scheduleName) throws SchedulerException {
        for (String triggerName : scheduler.getTriggerNames("default")) {
            Trigger trigger = scheduler.getTrigger(triggerName, "default");
            JobDetail detail = scheduler.getJobDetail(trigger.getJobName(), "default");

            JobDataMap dataMap = detail.getJobDataMap();
            if (scheduleName.equals(dataMap.get(SCHEDULE_NAME)) && externalId.equals(dataMap.get(EXTERNAL_ID))) {
                EnrollmentResponse enrollment = trackingService.getEnrollment(externalId, scheduleName);
                if (enrollment != null) {
                    storeAlertTimes(trigger, detail, enrollment.getReferenceDate());
                }
            }
        }
    }

    private void storeAlertTimes(Trigger trigger, JobDetail detail, LocalDate startDate) {
        LocalDate endDate = startDate.plusYears(2);
        List times = TriggerUtils.computeFireTimesBetween(trigger, new BaseCalendar(), startDate.toDate(), endDate.toDate());

        String windowName = String.valueOf(detail.getJobDataMap().get(WINDOW_NAME));
        MilestoneAlert milestoneAlert = (MilestoneAlert) detail.getJobDataMap().get(MILESTONE_NAME);
        String milestoneName = milestoneAlert.getMilestoneName();

        List<Date> existingAlertTimes = getTriggerTimesFor(milestoneName, windowName);
        existingAlertTimes.addAll(times);
    }

    private List<Date> getTriggerTimesFor(String milestoneName, String windowName) {
        Pair key = new Pair(milestoneName, windowName);

        if (!alertTimes.containsKey(key)) {
            alertTimes.put(key, new ArrayList<Date>());
        }

        return alertTimes.get(key);
    }

    private LocalDate nextFulfillmentDate() {
        if (fulfillmentDates.isEmpty()) {
            return DateUtil.today();
        }
        return new LocalDate(fulfillmentDates.pop());
    }

    public String name() {
        return name;
    }
}
