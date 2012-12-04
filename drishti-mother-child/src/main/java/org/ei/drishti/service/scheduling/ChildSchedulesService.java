package org.ei.drishti.service.scheduling;

import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.ChildInformation;
import org.ei.drishti.contract.Schedule;
import org.ei.drishti.repository.AllChildren;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang.StringUtils.join;
import static org.ei.drishti.common.AllConstants.ChildImmunizationCommCareFields.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.*;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.PREFERED_TIME_FOR_SCHEDULES;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;

@Service
public class ChildSchedulesService {
    private final ScheduleTrackingService scheduleTrackingService;
    private final AllChildren allChildren;
    private Map<String, Schedule> childSchedules;

    @Autowired
    public ChildSchedulesService(ScheduleTrackingService scheduleTrackingService, AllChildren allChildren) {
        this.scheduleTrackingService = scheduleTrackingService;
        this.allChildren = allChildren;
        initializeSchedules();
    }

    public void enrollChild(ChildInformation information) {
        enrollNonDependentModules(information);
        updateEnrollments(new ChildImmunizationUpdationRequest(information.caseId(), information.anmIdentifier(), join(information.immunizationsProvidedList(), " "), information.dateOfBirth().toString()));
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
                scheduleTrackingService.enroll(new EnrollmentRequest(information.caseId(), schedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES), information.dateOfBirth(), null, null, null, null, null));
            }
        }
    }

    private void enrollDependentModulesIfRequired(ChildImmunizationUpdationRequest information) {
        for (Schedule schedule : childSchedules.values()) {
            if (schedule.hasDependency() && !isImmunizationAlreadyProvided(information.caseId(),schedule)) {
                Schedule dependsOn = schedule.getDependencySchedule();
                if (information.immunizationsProvidedList().contains(dependsOn.getLastMilestone())
                        && isNotEnrolled(information.caseId(), schedule.name())) {
                    scheduleTrackingService.enroll(new EnrollmentRequest(information.caseId(), schedule.name(), new Time(PREFERED_TIME_FOR_SCHEDULES), information.immunizationsProvidedDate(), null, null, null, null, null));
                }
            }
        }
    }

    private boolean isImmunizationAlreadyProvided(String caseid, Schedule schedule) {
        List<String> alreadyProvidedImmunizations = allChildren.findByCaseId(caseid).immunizationsProvided();
        List<String> mileStones = schedule.getMileStones();
        for (String mileStone : mileStones) {
            if(!alreadyProvidedImmunizations.contains(mileStone))
                return false;
        }
        return true;
    }

    private void updateMilestonesForEnrolledSchedules(ChildImmunizationUpdationRequest information) {
        for (Schedule schedule : childSchedules.values()) {
            for (String mileStoneName : schedule.getMileStones()) {
                EnrollmentRecord record = scheduleTrackingService.getEnrollment(information.caseId(), schedule.name());
                if (record == null)
                    break;
                String currentMilestoneName = record.getCurrentMilestoneName();

                boolean isProvided = information.immunizationsProvidedList().contains(mileStoneName);

                if (isProvided && currentMilestoneName.equals(mileStoneName))
                    scheduleTrackingService.fulfillCurrentMilestone(information.caseId(), schedule.name(), information.immunizationsProvidedDate());
            }
        }
    }

    private boolean isNotEnrolled(String caseId, String scheduleName) {
        return scheduleTrackingService.getEnrollment(caseId, scheduleName) == null;
    }

    private void initializeSchedules() {
        List<String> bcgMileStones = unmodifiableList(asList(BCG_COMMCARE_VALUE));
        final Schedule bcg = new Schedule(CHILD_SCHEDULE_BCG, bcgMileStones);

        List<String> dpt1MileStones = unmodifiableList(asList(DPT_1_COMMCARE_VALUE));
        final Schedule dpt1 = new Schedule(CHILD_SCHEDULE_DPT1, dpt1MileStones);

        List<String> dpt2MileStones = unmodifiableList(asList(DPT_2_COMMCARE_VALUE));
        final Schedule dpt2 = new Schedule(CHILD_SCHEDULE_DPT2, dpt2MileStones).withDependencyOn(dpt1);

        List<String> dpt3MileStones = unmodifiableList(asList(DPT_3_COMMCARE_VALUE));
        final Schedule dpt3 = new Schedule(CHILD_SCHEDULE_DPT3, dpt3MileStones).withDependencyOn(dpt2);

        List<String> dptBooster1MileStones = unmodifiableList(asList(DPT_BOOSTER_1_COMMCARE_VALUE));
        final Schedule dptBooster1 = new Schedule(CHILD_SCHEDULE_DPT_BOOSTER1, dptBooster1MileStones).withDependencyOn(dpt3);

        List<String> dptBooster2MileStones = unmodifiableList(asList(DPT_BOOSTER_2_COMMCARE_VALUE));
        final Schedule dptBooster2 = new Schedule(CHILD_SCHEDULE_DPT_BOOSTER2, dptBooster2MileStones).withDependencyOn(dptBooster1);

        List<String> hepMilestones = unmodifiableList(asList(
                HEPATITIS_0_COMMCARE_VALUE,
                HEPATITIS_1_COMMCARE_VALUE,
                HEPATITIS_2_COMMCARE_VALUE,
                HEPATITIS_3_COMMCARE_VALUE));
        final Schedule hepatitis = new Schedule(CHILD_SCHEDULE_HEPATITIS, hepMilestones);


        List<String> measleMileStones = unmodifiableList(asList(MEASLES_COMMCARE_VALUE));
        final Schedule measles = new Schedule(CHILD_SCHEDULE_MEASLES, measleMileStones);

        List<String> measlesBoosterMileStones = unmodifiableList(asList(MEASLES_BOOSTER_COMMCARE_VALUE));
        final Schedule measlesBooster = new Schedule(CHILD_SCHEDULE_MEASLES_BOOSTER, measlesBoosterMileStones).withDependencyOn(measles);

        List<String> opvMileStones = unmodifiableList(asList(
                OPV_0_COMMCARE_VALUE,
                OPV_1_COMMCARE_VALUE,
                OPV_2_COMMCARE_VALUE,
                OPV_3_COMMCARE_VALUE
        ));
        final Schedule opv = new Schedule(CHILD_SCHEDULE_OPV, opvMileStones);


        childSchedules = unmodifiableMap(new HashMap<String, Schedule>() {{
            put(CHILD_SCHEDULE_BCG, bcg);
            put(CHILD_SCHEDULE_DPT1, dpt1);
            put(CHILD_SCHEDULE_DPT2, dpt2);
            put(CHILD_SCHEDULE_DPT3, dpt3);
            put(CHILD_SCHEDULE_DPT_BOOSTER1, dptBooster1);
            put(CHILD_SCHEDULE_DPT_BOOSTER2, dptBooster2);
            put(CHILD_SCHEDULE_HEPATITIS, hepatitis);
            put(CHILD_SCHEDULE_MEASLES, measles);
            put(CHILD_SCHEDULE_MEASLES_BOOSTER, measlesBooster);
            put(CHILD_SCHEDULE_OPV, opv);
        }});
    }


}
