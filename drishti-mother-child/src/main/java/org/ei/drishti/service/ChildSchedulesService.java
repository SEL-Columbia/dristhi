package org.ei.drishti.service;

import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.contract.ChildInformation;
import org.ei.drishti.contract.Schedule;
import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang.StringUtils.join;
import static org.motechproject.scheduletracking.api.domain.EnrollmentStatus.ACTIVE;

import static org.ei.drishti.scheduler.DrishtiSchedules.*;

@Service
public class ChildSchedulesService {
    private final ScheduleTrackingService scheduleTrackingService;
    private Map<String, Schedule> childSchedules;

    @Autowired
    public ChildSchedulesService(ScheduleTrackingService scheduleTrackingService) {
        this.scheduleTrackingService = scheduleTrackingService;
        initializeSchedules();
    }

    public void enrollChild(ChildInformation information){
        enrollNonDependentModules(information);
        updateEnrollments(new ChildImmunizationUpdationRequest(information.caseId(), information.anmIdentifier(), join(information.immunizationsProvided(), " "), LocalDate.now().toString()));
    }

    public void updateEnrollments(ChildImmunizationUpdationRequest information){
        updateMilestonesForEnrolledSchedules(information);
        //enrollDependentModulesIfRequired(information);
    }

    public void unenrollChild(String caseId) {
        List<EnrollmentRecord> openEnrollments = scheduleTrackingService.search(new EnrollmentsQuery().havingExternalId(caseId).havingState(ACTIVE));

        for (EnrollmentRecord enrollment : openEnrollments) {
            scheduleTrackingService.unenroll(caseId, Arrays.asList(enrollment.getScheduleName()));
        }
    }

    private void enrollNonDependentModules(ChildInformation information) {
        for (Schedule schedule : childSchedules.values()) {
            if(!schedule.hasDependency())
                scheduleTrackingService.enroll(new EnrollmentRequest(information.caseId(), schedule.getName(), null, information.dateOfBirth(), null, null, null, null, null));
        }
    }

//    private void enrollDependentModulesIfRequired(ChildImmunizationUpdationRequest information) {
//        for (Schedule schedule : childSchedules.values()) {
//            if(schedule.hasDependency()){
//                Schedule dependsOn = schedule.getDependencySchedule();
//                if(information.immunizationsProvidedList().contains(dependsOn.getLastMilestone())
//                        && isNotEnrolled(information.caseId(), schedule.getName())){
//                            scheduleTrackingService.enroll(new EnrollmentRequest(information.caseId(), schedule.getName(), null, information.immunizationsProvidedDate(), null, null, null, null, null));
//                }
//            }
//        }
//    }

    private void updateMilestonesForEnrolledSchedules(ChildImmunizationUpdationRequest information) {
        for (Schedule schedule : childSchedules.values()) {
            for (String mileStoneNameFromScheduler  : schedule.getMileStonesNameMapping().keySet()) {
                String mileStoneNameFromCommcareFrom = schedule.getMileStonesNameMapping().get(mileStoneNameFromScheduler);

                EnrollmentRecord record = scheduleTrackingService.getEnrollment(information.caseId(), schedule.getName());
                if (record == null)
                    break;
                String currentMilestoneName = record.getCurrentMilestoneName();

                boolean isProvided = information.immunizationsProvidedList().contains(mileStoneNameFromCommcareFrom);

                if(isProvided && currentMilestoneName.equals(mileStoneNameFromScheduler))
                    scheduleTrackingService.fulfillCurrentMilestone(information.caseId(), schedule.getName(), DateUtil.today());
            }
        }
    }

    private boolean isNotEnrolled(String caseId, String scheduleName) {
        return scheduleTrackingService.getEnrollment(caseId, scheduleName) == null;
    }

    private void initializeSchedules(){
        Map<String,String> bcgMileStones = unmodifiableMap(new LinkedHashMap<String, String>() {{
            put("REMINDER", "bcg");
        }});
        final Schedule bcg = new Schedule("BCG",bcgMileStones, null);

        Map<String,String> dptMileStones = unmodifiableMap(new LinkedHashMap<String, String>() {{
            put("DPT 0", "dpt_0");
            put("DPT 1", "dpt_1");
            put("DPT 2", "dpt_2");
            put("DPT 3", "dpt_3");
        }});
        final Schedule dpt = new Schedule("DPT",dptMileStones, null);

        Map<String,String> hepatitisMileStones = unmodifiableMap(new LinkedHashMap<String, String>() {{
            put("Hepatitis B1", "hepb_1");
            put("Hepatitis B2", "hepb_2");
            put("Hepatitis B3", "hepb_3");
            put("Hepatitis B4", "hepb_4");
        }});
        final Schedule hepatitis = new Schedule("Hepatitis",hepatitisMileStones, null);


        Map<String,String> measlesMiletones = unmodifiableMap(new LinkedHashMap<String, String>() {{
            put("REMINDER", "measles");
        }});
        final Schedule measles = new Schedule("Measles Vaccination", measlesMiletones, null);

        Map<String,String> measlesBoosterMileStones = unmodifiableMap(new LinkedHashMap<String, String>() {{
            put("REMINDER", "measlesbooster");
        }});
        final Schedule measlesBooster = new Schedule("Measles Booster", measlesBoosterMileStones, measles);

        Map<String,String> opvMileStones = unmodifiableMap(new LinkedHashMap<String, String>() {{
            put("OPV 0", "opv_0");
            put("OPV 1", "opv_1");
            put("OPV 2", "opv_2");
            put("OPV 3", "opv_3");
        }});
        final Schedule opv = new Schedule("OPV", opvMileStones, null);


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
