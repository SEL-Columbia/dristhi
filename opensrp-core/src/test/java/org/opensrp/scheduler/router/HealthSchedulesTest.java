package org.opensrp.scheduler.router;


import com.google.gson.JsonIOException;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.opensrp.dto.BeneficiaryType;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.scheduler.HealthSchedulerService;
import org.opensrp.scheduler.Schedule;
import org.opensrp.scheduler.Schedule.ActionType;
import org.opensrp.scheduler.ScheduleConfig;
import org.opensrp.scheduler.service.ActionService;
import org.opensrp.scheduler.service.ScheduleService;
import org.opensrp.util.TestResourceLoader;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.domain.WindowName.*;
import static org.opensrp.dto.AlertStatus.*;

public class HealthSchedulesTest extends TestResourceLoader {

    public static final String BENIFICIARY_TYPE = "benificiaryType";
    public static final String ENTITY_ID = "entityId";
    public static final String PROVIDER_ID = "providerId";
    public static final String SCHEDULE_NAME = "schedule";
    public static final String MILESTONE = "milestone";
    public static final String REASONFOR_CLOSE = "reasonforClose";
    public static final String FORM_SUBMISSION_ID = "formSubmissionId";
    DateTime startOfDueWindow = new DateTime(0l);
    DateTime startOfLateWindow = new DateTime(1l);
    DateTime startOfMaxWindow = new DateTime(2l);

    public HealthSchedulesTest() throws IOException {
        super();
    }

    private HealthSchedulerService sch;
    private ScheduleConfig scheduleConfig;
    @Mock
    private ScheduleService schService;
    @Mock
    private ActionService actionService;

    @Before
    public void setUp() throws IOException, JSONException {
        initMocks(this);

        scheduleConfig = new ScheduleConfig("/schedules/schedule-config.xls");
        sch = new HealthSchedulerService(actionService, schService, scheduleConfig);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindAutomatedSchedules() throws JsonIOException, IOException {
        FormSubmission fs = getFormSubmissionFor("child_enrollment", 3);
        List<Schedule> s = sch.findAutomatedSchedules(fs.formName());
        assertThat(s, hasItem(Matchers.<Schedule>allOf(
                Matchers.<Schedule>hasProperty("schedule", equalTo("PENTAVALENT 1")),
                Matchers.<Schedule>hasProperty("milestone", equalTo("penta1")),
                Matchers.<Schedule>hasProperty("action", equalTo(ActionType.enroll)),
                Matchers.<Schedule>hasProperty("entityType", equalTo("pkchild")),
                Matchers.<Schedule>hasProperty("triggerDateFields", hasItem("dob"))
        )));
    }

    @Test
    public void testProperAlertRouting() {

        sch.alertFor(late.name(), BENIFICIARY_TYPE, ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, MILESTONE,
                startOfDueWindow, startOfLateWindow, startOfMaxWindow);
        verify(actionService).alertForBeneficiary(BENIFICIARY_TYPE, ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, MILESTONE, urgent, startOfLateWindow, startOfMaxWindow);

        sch.alertFor(earliest.name(), BENIFICIARY_TYPE, ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, MILESTONE,
                startOfDueWindow, startOfLateWindow, startOfMaxWindow);
        verify(actionService).alertForBeneficiary(BENIFICIARY_TYPE, ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, MILESTONE, upcoming, startOfDueWindow, startOfLateWindow);

        sch.alertFor(due.name(), BENIFICIARY_TYPE, ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, MILESTONE,
                startOfDueWindow, startOfLateWindow, startOfMaxWindow);
        verify(actionService).alertForBeneficiary(BENIFICIARY_TYPE, ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, MILESTONE, normal, startOfDueWindow, startOfLateWindow);

        sch.alertFor(max.name(), BENIFICIARY_TYPE, ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, MILESTONE,
                startOfDueWindow, startOfLateWindow, startOfMaxWindow);
        verify(actionService).alertForBeneficiary(BENIFICIARY_TYPE, ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, MILESTONE, expired, startOfMaxWindow, startOfMaxWindow);
    }

    @Test
    public void testAlertRoutingWithAlertStatus() {
        sch.alertFor(BENIFICIARY_TYPE, ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, MILESTONE, upcoming,
                startOfDueWindow, startOfLateWindow, startOfMaxWindow);
        verify(actionService).alertForBeneficiary(BENIFICIARY_TYPE, ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, MILESTONE, upcoming, startOfLateWindow, startOfMaxWindow);
    }

    @Test
    public void testGettingEnrollmentRecord() {
        sch.getEnrollmentRecord(ENTITY_ID, SCHEDULE_NAME);
        verify(schService).getEnrollmentRecord(ENTITY_ID, SCHEDULE_NAME);
    }

    @Test
    public void testGettingEnrollment() {
        sch.getEnrollment(ENTITY_ID, SCHEDULE_NAME);
        verify(schService).getEnrollment(ENTITY_ID, SCHEDULE_NAME);
    }

    @Test
    public void testActiveEnrollment() {
        sch.getActiveEnrollment(ENTITY_ID, SCHEDULE_NAME);
        verify(schService).getActiveEnrollment(ENTITY_ID, SCHEDULE_NAME);
    }

    @Test
    public void testIsNotEnrolled() {
        Enrollment enrollment = mock(Enrollment.class);
        when(schService.getEnrollment(ENTITY_ID, SCHEDULE_NAME)).thenReturn(enrollment);
        boolean actual = sch.isNotEnrolled(ENTITY_ID, SCHEDULE_NAME);
        assertFalse(actual);

        when(schService.getEnrollment(ENTITY_ID, SCHEDULE_NAME)).thenReturn(null);
        actual = sch.isNotEnrolled(ENTITY_ID, SCHEDULE_NAME);
        assertTrue(actual);

    }

    @Test
    public void testFindActiveSchedules() {
        sch.findActiveSchedules(ENTITY_ID);
        verify(schService).findOpenEnrollmentNames(ENTITY_ID);
    }

    @Test
    public void testFindActiveEnrollments() {
        sch.findActiveEnrollments(ENTITY_ID);
        verify(schService).findOpenEnrollments(ENTITY_ID);
    }

    @Test
    public void testCloseBeneficiary() {
        sch.closeBeneficiary(BeneficiaryType.mother, ENTITY_ID, PROVIDER_ID, REASONFOR_CLOSE);
        verify(actionService).closeBeneficiary(BeneficiaryType.mother, ENTITY_ID, PROVIDER_ID, REASONFOR_CLOSE);
    }

    @Test
    public void testUnEnrollFromAllSchedules() {
        List<String> activeSchedules = asList("1","2");
        when(schService.findOpenEnrollmentNames(ENTITY_ID)).thenReturn(activeSchedules);
        sch.unEnrollFromAllSchedules(ENTITY_ID, FORM_SUBMISSION_ID);

        verify(schService).unenroll(ENTITY_ID, activeSchedules, FORM_SUBMISSION_ID);
        verify(actionService).markAllAlertsAsInactive(ENTITY_ID);
    }

    @Test
    public void testUnEnrollAndCloeSchedule() {
        sch.unEnrollAndCloseSchedule(ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, startOfDueWindow.toLocalDate(), FORM_SUBMISSION_ID);
        verify(schService).unenroll(ENTITY_ID, SCHEDULE_NAME, FORM_SUBMISSION_ID);
        verify(actionService).markAlertAsClosed(PROVIDER_ID, ENTITY_ID, SCHEDULE_NAME, startOfDueWindow.toLocalDate().toString());
    }

    @Test
    public void testUnEnrollFroSchedule() {
        sch.unEnrollFromSchedule(ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, FORM_SUBMISSION_ID);
        verify(schService).unenroll(ENTITY_ID, SCHEDULE_NAME, FORM_SUBMISSION_ID);
        verify(actionService).markAlertAsInactive(PROVIDER_ID, ENTITY_ID, SCHEDULE_NAME);
    }

    @Test
    public void testForceFullfillMilestonesAndCloseAlert() {
        sch.forceFullfillMilestonesAndCloseAlert(ENTITY_ID, SCHEDULE_NAME, startOfDueWindow.toLocalDate());
        verify(schService).fulfillMilestone(ENTITY_ID, SCHEDULE_NAME, startOfDueWindow.toLocalDate(), "");
        verify(actionService).markAlertAsClosed(ENTITY_ID, SCHEDULE_NAME, startOfDueWindow.toLocalDate().toString());
    }

    @Test
    public void testFullFillMilestoneAndCloseAlert() {
        sch.fullfillMilestoneAndCloseAlert(ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, startOfDueWindow.toLocalDate(), FORM_SUBMISSION_ID);
        verify(schService).fulfillMilestone(ENTITY_ID, SCHEDULE_NAME, startOfDueWindow.toLocalDate(), FORM_SUBMISSION_ID);
        verify(actionService).markAlertAsClosed(ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, startOfDueWindow.toLocalDate().toString());
    }

    @Test
    public void testFullFillMilestoneAndCloseAlertWithMilestone() {
        sch.fullfillMilestoneAndCloseAlert(ENTITY_ID, PROVIDER_ID, SCHEDULE_NAME, MILESTONE, startOfDueWindow.toLocalDate(), FORM_SUBMISSION_ID);
        verify(schService).fulfillMilestone(ENTITY_ID, SCHEDULE_NAME, startOfDueWindow.toLocalDate(), FORM_SUBMISSION_ID);
        verify(actionService).markAlertAsClosed(ENTITY_ID, PROVIDER_ID, MILESTONE, startOfDueWindow.toLocalDate().toString());
    }


    @Test
    public void testEnrollmentIntoSchedule() {
        sch.enrollIntoSchedule(ENTITY_ID,SCHEDULE_NAME, MILESTONE,"", FORM_SUBMISSION_ID);
        verify(schService).enroll(ENTITY_ID, SCHEDULE_NAME, MILESTONE, "", FORM_SUBMISSION_ID);

        testEnrollmentWithOutMilestone();
    }

    @Test
    public void testEnrollmentWithOutMilestone() {
        sch.enrollIntoSchedule(ENTITY_ID, SCHEDULE_NAME, "", FORM_SUBMISSION_ID);
        verify(schService).enroll(ENTITY_ID, SCHEDULE_NAME, "", FORM_SUBMISSION_ID);
    }

    @Test
    public void testEnrollmentWithOutMilestoneAndLocalDate() {
        sch.enrollIntoSchedule(ENTITY_ID, SCHEDULE_NAME, startOfLateWindow.toLocalDate(), FORM_SUBMISSION_ID);
        verify(schService).enroll(ENTITY_ID, SCHEDULE_NAME, startOfLateWindow.toLocalDate().toString(), FORM_SUBMISSION_ID);
    }


}
