package org.ei.drishti.service;

import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.ChildInformation;
import org.ei.drishti.contract.Schedule;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang.StringUtils.join;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;

import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.*;

@Service
public class ChildSchedulesService {
    private final ScheduleTrackingService scheduleTrackingService;
    private Map<String, Schedule> childSchedules;

    @Autowired
    public ChildSchedulesService(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
        initializeSchedules();
    }

    public void enrollChild(ChildInformation information) {
        enrollNonDependentModules(information);
        updateEnrollments(new ChildImmunizationUpdationRequest(information.caseId(), information.anmIdentifier(), join(information.immunizationsProvided(), " "), LocalDate.now().toString()));
    }

    public void updateEnrollments(ChildImmunizationUpdationRequest information) {
        enrollDependentModulesIfRequired(information);
        updateMilestonesForEnrolledSchedules(information);
    }

    public void unenrollChild(String caseId) {
        List<EnrollmentRecord> openEnrollments = scheduleTrackingService.search(new EnrollmentsQuery().havingExternalId(caseId).havingState(ACTIVE));

        for (EnrollmentRecord enrollment : openEnrollments) {
            scheduleTrackingService.unenroll(caseId, Arrays.asList(enrollment.getScheduleName()));
        }
    }

    private void enrollNonDependentModules(ChildInformation information) {
        for (Schedule schedule : childSchedules.values()) {
            if (!schedule.hasDependency()) {
                scheduleTrackingService.enroll(new EnrollmentRequest(information.caseId(), schedule.getName(), new Time(PREFERED_TIME_FOR_SCHEDULES), information.dateOfBirth(), null, null, null, null, null));
            }
        }
    }

    private void enrollDependentModulesIfRequired(ChildImmunizationUpdationRequest information) {
        for (Schedule schedule : childSchedules.values()) {
            if (schedule.hasDependency()) {
                Schedule dependsOn = schedule.getDependencySchedule();
                if (information.immunizationsProvidedList().contains(dependsOn.getLastMilestone())
                        && isNotEnrolled(information.caseId(), schedule.getName())) {
                    scheduleTrackingService.enroll(new EnrollmentRequest(information.caseId(), schedule.getName(), new Time(PREFERED_TIME_FOR_SCHEDULES), information.immunizationsProvidedDate(), null, null, null, null, null));
                }
            }
        }
    }

    private void updateMilestonesForEnrolledSchedules(ChildImmunizationUpdationRequest information) {
        for (Schedule schedule : childSchedules.values()) {
            for (String mileStoneName : schedule.getMileStones()) {
                EnrollmentRecord record = scheduleTrackingService.getEnrollment(information.caseId(), schedule.getName());
                if (record == null)
                    break;
                String currentMilestoneName = record.getCurrentMilestoneName();

                boolean isProvided = information.immunizationsProvidedList().contains(mileStoneName);

                if (isProvided && currentMilestoneName.equals(mileStoneName))
                    scheduleTrackingService.fulfillCurrentMilestone(information.caseId(), schedule.getName(), DateUtil.today());
            }
        }
    }

    private boolean isNotEnrolled(String caseId, String scheduleName) {
        return scheduleTrackingService.getEnrollment(caseId, scheduleName) == null;
    }

    private void initializeSchedules() {
        List<String> bcgMileStones = unmodifiableList(asList(CHILD_SCHEDULE_BCG_MILESTONE));
        final Schedule bcg = new Schedule(CHILD_SCHEDULE_BCG, bcgMileStones);

        List<String> dptMileStones = unmodifiableList(asList(
                CHILD_SCHEDULE_DPT_MILESTONE_0,
                CHILD_SCHEDULE_DPT_MILESTONE_1,
                CHILD_SCHEDULE_DPT_MILESTONE_2,
                CHILD_SCHEDULE_DPT_MILESTONE_3));
        final Schedule dpt = new Schedule(CHILD_SCHEDULE_DPT, dptMileStones);

        List<String> hepMilestones = unmodifiableList(asList(
                CHILD_SCHEDULE_HEPATITIS_MILESTONE_1,
                CHILD_SCHEDULE_HEPATITIS_MILESTONE_2,
                CHILD_SCHEDULE_HEPATITIS_MILESTONE_3,
                CHILD_SCHEDULE_HEPATITIS_MILESTONE_4));
        final Schedule hepatitis = new Schedule(CHILD_SCHEDULE_HEPATITIS, hepMilestones);


        List<String> measleMileStones = unmodifiableList(asList(CHILD_SCHEDULE_MEASLES_MILESTONE));
        final Schedule measles = new Schedule(CHILD_SCHEDULE_MEASLES, measleMileStones);

        List<String> measlesBoosterMileStones = unmodifiableList(asList(CHILD_SCHEDULE_MEASLES_BOOSTER_MILESTONE));
        final Schedule measlesBooster = new Schedule(CHILD_SCHEDULE_MEASLES_BOOSTER, measlesBoosterMileStones).withDependencyOn(measles);

        List<String> opvMileStones = unmodifiableList(asList(
                CHILD_SCHEDULE_OPV_MILESTONE_0,
                CHILD_SCHEDULE_OPV_MILESTONE_1,
                CHILD_SCHEDULE_OPV_MILESTONE_2,
                CHILD_SCHEDULE_OPV_MILESTONE_3
        ));
        final Schedule opv = new Schedule(CHILD_SCHEDULE_OPV, opvMileStones);


        childSchedules = unmodifiableMap(new HashMap<String, Schedule>() {{
            put(CHILD_SCHEDULE_BCG, bcg);
            put(CHILD_SCHEDULE_DPT, dpt);
            put(CHILD_SCHEDULE_HEPATITIS, hepatitis);
            put(CHILD_SCHEDULE_MEASLES, measles);
            put(CHILD_SCHEDULE_MEASLES_BOOSTER, measlesBooster);
            put(CHILD_SCHEDULE_OPV, opv);
        }});
    }


}
