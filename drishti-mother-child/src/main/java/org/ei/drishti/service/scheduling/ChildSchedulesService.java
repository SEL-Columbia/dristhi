package org.ei.drishti.service.scheduling;

import org.ei.drishti.contract.Schedule;
import org.ei.drishti.domain.Child;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.service.ActionService;
import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.ei.drishti.common.AllConstants.ChildImmunizationFields.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.*;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;

@Service
public class ChildSchedulesService {
    private static Logger logger = LoggerFactory.getLogger(ChildSchedulesService.class.toString());

    private final ScheduleTrackingService scheduleTrackingService;
    private final AllChildren allChildren;
    private final ScheduleService scheduleService;
    private ActionService actionService;
    private Map<String, Schedule> childSchedules;

    @Autowired
    public ChildSchedulesService(ScheduleTrackingService scheduleTrackingService, AllChildren allChildren,
                                 ScheduleService scheduleService, ActionService actionService) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.allChildren = allChildren;
        this.scheduleService = scheduleService;
        this.actionService = actionService;
        initializeSchedules();
    }

    public void enrollChild(Child child) {
        enrollNonDependentModules(child.caseId(), LocalDate.parse(child.dateOfBirth()));
        enrollDependentModulesIfRequired(child.caseId(), Collections.<String>emptyList(), child.immunizationsGiven(), child.dateOfBirth());
        updateMilestonesForEnrolledSchedules(child.caseId(), child.anmIdentifier(), child.immunizationsGiven(), child.dateOfBirth());
    }

    public void updateEnrollments(String entityId, List<String> previousImmunizations) {
        Child child = allChildren.findByCaseId(entityId);
        enrollDependentModulesIfRequired(child.caseId(), previousImmunizations, child.immunizationsGiven(), child.immunizationDate());
        updateMilestonesForEnrolledSchedules(child.caseId(), child.anmIdentifier(), child.immunizationsGiven(), child.immunizationDate());
    }

    public void unenrollChild(String id) {
        List<EnrollmentRecord> openEnrollments = scheduleTrackingService.search(new EnrollmentsQuery().havingExternalId(id).havingState(ACTIVE));

        for (EnrollmentRecord enrollment : openEnrollments) {
            logger.info(format("Un-enrolling child from schedule: {0}, entityId: {0}", enrollment.getScheduleName(), id));
            scheduleTrackingService.unenroll(id, Arrays.asList(enrollment.getScheduleName()));
        }
    }

    private void enrollNonDependentModules(String id, LocalDate dateOfBirth) {
        for (Schedule schedule : childSchedules.values()) {
            if (!schedule.hasDependency()) {
                logger.info(format("Enrolling child to schedule: {0}, entityId: {1}, referenceDate: {2}", schedule.name(), id, dateOfBirth));
                scheduleService.enroll(id, schedule.name(), dateOfBirth.toString());
            }
        }
    }

    private void enrollDependentModulesIfRequired(String id, List<String> previousImmunizations, List<String> immunizationsGiven, String immunizationDate) {
        for (Schedule schedule : childSchedules.values()) {
            if (schedule.hasDependency() && !isImmunizationAlreadyProvided(schedule, previousImmunizations)) {
                Schedule dependsOn = schedule.getDependencySchedule();
                if (immunizationsGiven.contains(dependsOn.getLastMilestone())
                        && isNotEnrolled(id, schedule.name())) {
                    logger.info(format("Enrolling child to schedule: {0}, entityId: {1}, referenceDate: {2}", schedule.name(), id, immunizationDate));
                    scheduleService.enroll(id, schedule.name(), immunizationDate);
                }
            }
        }
    }

    private boolean isImmunizationAlreadyProvided(Schedule schedule, List<String> previousImmunizations) {
        List<String> mileStones = schedule.getMileStones();
        for (String mileStone : mileStones) {
            if (!previousImmunizations.contains(mileStone))
                return false;
        }
        return true;
    }

    private void updateMilestonesForEnrolledSchedules(String id, String anmIdentifier, List<String> immunizationsGiven, String immunizationDate) {
        for (Schedule schedule : childSchedules.values()) {
            for (String mileStoneName : schedule.getMileStones()) {
                EnrollmentRecord record = scheduleTrackingService.getEnrollment(id, schedule.name());
                if (record == null)
                    break;
                String currentMilestoneName = record.getCurrentMilestoneName();

                boolean isProvided = immunizationsGiven.contains(mileStoneName);

                if (isProvided && currentMilestoneName.equals(mileStoneName)) {
                    logger.info(format("Fulfilling current milestone of schedule: {0}, milestone: {1}, entityId: {2}, completionDate: {3}", schedule.name(),
                            mileStoneName, id, immunizationDate));
                    scheduleTrackingService.fulfillCurrentMilestone(id, schedule.name(), LocalDate.parse(immunizationDate));
                    actionService.markAlertAsClosed(id, anmIdentifier, mileStoneName, immunizationDate);
                }
            }
        }
    }

    private boolean isNotEnrolled(String entityId, String scheduleName) {
        return scheduleTrackingService.getEnrollment(entityId, scheduleName) == null;
    }

    private void initializeSchedules() {
        List<String> bcgMileStones = unmodifiableList(asList(BCG_VALUE));
        final Schedule bcg = new Schedule(CHILD_SCHEDULE_BCG, bcgMileStones);

        List<String> dptBooster1MileStones = unmodifiableList(asList(DPT_BOOSTER_1_VALUE));
        final Schedule dptBooster1 = new Schedule(CHILD_SCHEDULE_DPT_BOOSTER1, dptBooster1MileStones);

        List<String> dptBooster2MileStones = unmodifiableList(asList(DPT_BOOSTER_2_VALUE));
        final Schedule dptBooster2 = new Schedule(CHILD_SCHEDULE_DPT_BOOSTER2, dptBooster2MileStones).withDependencyOn(dptBooster1);

        List<String> hepMilestones = unmodifiableList(asList(
                HEPATITIS_0_VALUE,
                HEPATITIS_1_VALUE,
                HEPATITIS_2_VALUE,
                HEPATITIS_3_VALUE));
        final Schedule hepatitis = new Schedule(CHILD_SCHEDULE_HEPATITIS, hepMilestones);


        List<String> measlesMileStones = unmodifiableList(asList(MEASLES_VALUE));
        final Schedule measles = new Schedule(CHILD_SCHEDULE_MEASLES, measlesMileStones);

        List<String> measlesBoosterMileStones = unmodifiableList(asList(MEASLES_BOOSTER_VALUE));
        final Schedule measlesBooster = new Schedule(CHILD_SCHEDULE_MEASLES_BOOSTER, measlesBoosterMileStones).withDependencyOn(measles);

        List<String> opvMileStones = unmodifiableList(asList(
                OPV_0_VALUE,
                OPV_1_VALUE,
                OPV_2_VALUE,
                OPV_3_VALUE
        ));
        final Schedule opv = new Schedule(CHILD_SCHEDULE_OPV, opvMileStones);


        childSchedules = unmodifiableMap(new HashMap<String, Schedule>() {{
            put(CHILD_SCHEDULE_BCG, bcg);
            put(CHILD_SCHEDULE_DPT_BOOSTER1, dptBooster1);
            put(CHILD_SCHEDULE_DPT_BOOSTER2, dptBooster2);
            put(CHILD_SCHEDULE_HEPATITIS, hepatitis);
            put(CHILD_SCHEDULE_MEASLES, measles);
            put(CHILD_SCHEDULE_MEASLES_BOOSTER, measlesBooster);
            put(CHILD_SCHEDULE_OPV, opv);
        }});
    }
}
