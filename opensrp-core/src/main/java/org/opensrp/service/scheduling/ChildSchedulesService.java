package org.opensrp.service.scheduling;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.BCG_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.DPT_BOOSTER_1_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.DPT_BOOSTER_2_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.MEASLES_BOOSTER_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.MEASLES_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.OPV_0_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.OPV_1_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.OPV_2_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.OPV_3_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.OPV_BOOSTER_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.PENTAVALENT_1_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.PENTAVALENT_2_VALUE;
import static org.opensrp.common.AllConstants.ChildImmunizationFields.PENTAVALENT_3_VALUE;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_BCG;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_DPT_BOOSTER1;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_DPT_BOOSTER2;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_MEASLES;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_MEASLES_BOOSTER;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_OPV_0_AND_1;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_OPV_2;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_OPV_3;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_OPV_BOOSTER;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_PENTAVALENT_1;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_PENTAVALENT_2;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.CHILD_SCHEDULE_PENTAVALENT_3;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.opensrp.domain.Child;
import org.opensrp.repository.AllChildren;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChildSchedulesService {
    private static Logger logger = LoggerFactory.getLogger(ChildSchedulesService.class.toString());

    private final AllChildren allChildren;
    private Map<String, Schedule> childSchedules;
    private HealthSchedulerService scheduler;

    @Autowired
    public ChildSchedulesService(AllChildren allChildren, HealthSchedulerService scheduler) {
        this.scheduler = scheduler;
        this.allChildren = allChildren;
        initializeSchedules();
    }

    public void enrollChild(Child child) {
        enrollNonDependentModules(child.caseId(), child.dateOfBirth());
        enrollDependentModulesIfRequired(child.caseId(), Collections.<String>emptyList(), child.immunizationsGiven(), child.dateOfBirth());
        updateMilestonesForEnrolledSchedules(child.caseId(), child.anmIdentifier(), child.immunizationsGiven(), child.dateOfBirth());
    }

    public void updateEnrollments(String entityId, List<String> previousImmunizations) {
        Child child = allChildren.findByCaseId(entityId);
        enrollDependentModulesIfRequired(child.caseId(), previousImmunizations, child.immunizationsGiven(), child.immunizationDate());
        updateMilestonesForEnrolledSchedules(child.caseId(), child.anmIdentifier(), child.immunizationsGiven(), child.immunizationDate());
    }

    //#TODO: Remove this duplicated code
    //TODO MAIMOONA: CHANGED IT SO VERIFY WITH KIRRRRNNNNN
    public void unenrollChild(String id) {
    	scheduler.unEnrollFromAllSchedules(id);
    }

    private void enrollNonDependentModules(String id, String dateOfBirth) {
        for (Schedule schedule : childSchedules.values()) {
            if (!schedule.hasDependency()) {
                logger.info(format("Enrolling child to schedule: {0}, entityId: {1}, referenceDate: {2}", schedule.name(), id, dateOfBirth));
                scheduler.enrollIntoSchedule(id, schedule.name(), dateOfBirth);
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
                    scheduler.enrollIntoSchedule(id, schedule.name(), immunizationDate);
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
                EnrollmentRecord record = scheduler.getEnrollment(id, schedule.name());
                if (record == null)
                    break;
                String currentMilestoneName = record.getCurrentMilestoneName();

                boolean isProvided = immunizationsGiven.contains(mileStoneName);

                if (isProvided && currentMilestoneName.equals(mileStoneName)) {
                    logger.info(format("Fulfilling current milestone of schedule: {0}, milestone: {1}, entityId: {2}, completionDate: {3}", schedule.name(),
                            mileStoneName, id, immunizationDate));
                    scheduler.fullfillMilestoneAndCloseAlert(id, anmIdentifier, schedule.name(), mileStoneName, LocalDate.parse(immunizationDate));
                }
            }
        }
    }

    private boolean isNotEnrolled(String entityId, String scheduleName) {
        return scheduler.isNotEnrolled(entityId, scheduleName);
    }

    private void initializeSchedules() {
        List<String> bcgMileStones = unmodifiableList(asList(BCG_VALUE));
        final Schedule bcg = new Schedule(CHILD_SCHEDULE_BCG, bcgMileStones);

        List<String> dptBooster1MileStones = unmodifiableList(asList(DPT_BOOSTER_1_VALUE));
        final Schedule dptBooster1 = new Schedule(CHILD_SCHEDULE_DPT_BOOSTER1, dptBooster1MileStones);

        List<String> dptBooster2MileStones = unmodifiableList(asList(DPT_BOOSTER_2_VALUE));
        final Schedule dptBooster2 = new Schedule(CHILD_SCHEDULE_DPT_BOOSTER2, dptBooster2MileStones).withDependencyOn(dptBooster1);

        List<String> measlesMileStones = unmodifiableList(asList(MEASLES_VALUE));
        final Schedule measles = new Schedule(CHILD_SCHEDULE_MEASLES, measlesMileStones);

        List<String> measlesBoosterMileStones = unmodifiableList(asList(MEASLES_BOOSTER_VALUE));
        final Schedule measlesBooster = new Schedule(CHILD_SCHEDULE_MEASLES_BOOSTER, measlesBoosterMileStones).withDependencyOn(measles);

        List<String> opv0And1MileStones = unmodifiableList(asList(
                OPV_0_VALUE,
                OPV_1_VALUE
        ));
        final Schedule opv0And1 = new Schedule(CHILD_SCHEDULE_OPV_0_AND_1, opv0And1MileStones);

        List<String> opv2MileStone = unmodifiableList(asList(OPV_2_VALUE));
        final Schedule opv2 = new Schedule(CHILD_SCHEDULE_OPV_2, opv2MileStone).withDependencyOn(opv0And1);

        List<String> opv3MileStone = unmodifiableList(asList(OPV_3_VALUE));
        final Schedule opv3 = new Schedule(CHILD_SCHEDULE_OPV_3, opv3MileStone).withDependencyOn(opv2);

        List<String> opvBoosterMileStone = unmodifiableList(asList(OPV_BOOSTER_VALUE));
        final Schedule opvBooster = new Schedule(CHILD_SCHEDULE_OPV_BOOSTER, opvBoosterMileStone).withDependencyOn(opv3);

        List<String> pentavalent1Milestone = unmodifiableList(asList(PENTAVALENT_1_VALUE));
        final Schedule pentavalent1 = new Schedule(CHILD_SCHEDULE_PENTAVALENT_1, pentavalent1Milestone);

        List<String> pentavalent2Milestone = unmodifiableList(asList(PENTAVALENT_2_VALUE));
        final Schedule pentavalent2 = new Schedule(CHILD_SCHEDULE_PENTAVALENT_2, pentavalent2Milestone).withDependencyOn(pentavalent1);

        List<String> pentavalent3Milestone = unmodifiableList(asList(PENTAVALENT_3_VALUE));
        final Schedule pentavalent3 = new Schedule(CHILD_SCHEDULE_PENTAVALENT_3, pentavalent3Milestone).withDependencyOn(pentavalent2);


        childSchedules = unmodifiableMap(new TreeMap<String, Schedule>() {
			private static final long serialVersionUID = 1L;

		{
            put(CHILD_SCHEDULE_BCG, bcg);
            put(CHILD_SCHEDULE_DPT_BOOSTER1, dptBooster1);
            put(CHILD_SCHEDULE_DPT_BOOSTER2, dptBooster2);
            put(CHILD_SCHEDULE_MEASLES, measles);
            put(CHILD_SCHEDULE_MEASLES_BOOSTER, measlesBooster);
            put(CHILD_SCHEDULE_OPV_0_AND_1, opv0And1);
            put(CHILD_SCHEDULE_OPV_2, opv2);
            put(CHILD_SCHEDULE_OPV_3, opv3);
            put(CHILD_SCHEDULE_OPV_BOOSTER, opvBooster);
            put(CHILD_SCHEDULE_PENTAVALENT_1, pentavalent1);
            put(CHILD_SCHEDULE_PENTAVALENT_2, pentavalent2);
            put(CHILD_SCHEDULE_PENTAVALENT_3, pentavalent3);
        }});
    }
}
