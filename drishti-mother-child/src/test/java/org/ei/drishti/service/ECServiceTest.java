package org.ei.drishti.service;

import org.ei.drishti.contract.EligibleCoupleCloseRequest;
import org.ei.drishti.contract.EligibleCoupleRegistrationRequest;
import org.ei.drishti.contract.FamilyPlanningUpdateRequest;
import org.ei.drishti.contract.OutOfAreaANCRegistrationRequest;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.reporting.ECReportingService;
import org.ei.drishti.service.scheduling.ECSchedulingService;
import org.ei.drishti.util.IdGenerator;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.ei.drishti.common.AllConstants.DETAILS_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.EC_SCHEDULE_FP_COMPLICATION_MILESTONE;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECServiceTest {
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private ActionService actionService;
    @Mock
    private IdGenerator idGenerator;
    @Mock
    private ECReportingService reportingService;
    @Mock
    private ECSchedulingService schedulingService;

    private ECService ecService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ecService = new ECService(allEligibleCouples, actionService, reportingService, idGenerator, schedulingService);
    }

    @Test
    public void shouldRegisterEligibleCouple() throws Exception {
        Map<String, Map<String, String>> extraData = create("details", Collections.<String, String>emptyMap())
                .put(REPORT_EXTRA_DATA_KEY_NAME, mapOf("someKey", "someValue"))
                .put(DETAILS_EXTRA_DATA_KEY_NAME, mapOf("someKey", "someValue")).map();

        EligibleCoupleRegistrationRequest eligibleCoupleRegistrationRequest = new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "Condom", "No");
        ecService.registerEligibleCouple(eligibleCoupleRegistrationRequest, extraData);

        EligibleCouple expectedCouple = new EligibleCouple("CASE X", "EC Number 1").withCouple("Wife 1", "Husband 1")
                .withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(extraData.get("details"));
        verify(allEligibleCouples).register(expectedCouple);
        verify(reportingService).registerEC(new SafeMap(extraData.get(REPORT_EXTRA_DATA_KEY_NAME)));
        verify(actionService).registerEligibleCouple("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", extraData.get("details"));
        verify(schedulingService).enrollToFPComplications(eligibleCoupleRegistrationRequest, extraData.get(DETAILS_EXTRA_DATA_KEY_NAME));
    }

    @Test
    public void shouldRegisterEligibleCoupleForOutOfAreaANC() throws Exception {
        Map<String, Map<String, String>> extraData = mapOf("details", Collections.<String, String>emptyMap());
        UUID ecCaseId = randomUUID();
        when(idGenerator.generateUUID()).thenReturn(ecCaseId);

        ecService.registerEligibleCoupleForOutOfAreaANC(new OutOfAreaANCRegistrationRequest("CASE X", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", "TC 1", "2012-05-05", "9876543210"), extraData);

        EligibleCouple couple = new EligibleCouple(ecCaseId.toString(), "0").withCouple("Wife 1", "Husband 1")
                .withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(extraData.get("details")).asOutOfArea();
        verify(allEligibleCouples).register(couple);
    }

    @Test
    public void shouldUpdateExistingDetailsBlobInECAndCreateAnActionForFPMethodUpdate() throws Exception {
        Map<String, String> existingDetails = mapOf("existingThing", "existingValue");
        EligibleCouple existingCoupleBeforeUpdate = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(existingDetails);

        Map<String, String> updatedDetails = create("currentMethod", "CONDOM").put("existingThing", "existingValue").map();
        EligibleCouple existingCoupleAfterUpdate = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(updatedDetails);

        when(allEligibleCouples.findByCaseId("CASE X")).thenReturn(existingCoupleBeforeUpdate);
        when(allEligibleCouples.updateDetails("CASE X", mapOf("currentMethod", "CONDOM"))).thenReturn(existingCoupleAfterUpdate);

        ecService.updateFamilyPlanningMethod(new FamilyPlanningUpdateRequest("CASE X", "ANM X"), mapOf("details", mapOf("currentMethod", "CONDOM")));

        verify(allEligibleCouples).updateDetails("CASE X", mapOf("currentMethod", "CONDOM"));
        verify(allEligibleCouples).findByCaseId("CASE X");
        verify(actionService).updateEligibleCoupleDetails("CASE X", "ANM X", updatedDetails);
    }

    @Test
    public void shouldReportFPMethodChangeWhenFPMethodIsUpdated() throws Exception {
        Map<String, String> existingDetails = mapOf("existingThing", "existingValue");
        EligibleCouple existingCoupleBeforeUpdate = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(existingDetails);
        Map<String, String> updatedDetails = create("currentMethod", "CONDOM").put("existingThing", "existingValue").map();
        EligibleCouple existingCoupleAfterUpdate = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(updatedDetails);
        when(allEligibleCouples.findByCaseId("CASE X")).thenReturn(existingCoupleBeforeUpdate);
        when(allEligibleCouples.updateDetails("CASE X", mapOf("currentMethod", "CONDOM"))).thenReturn(existingCoupleAfterUpdate);

        ecService.updateFamilyPlanningMethod(new FamilyPlanningUpdateRequest("CASE X", "ANM X"), create("details", mapOf("currentMethod", "CONDOM")).put(REPORT_EXTRA_DATA_KEY_NAME, mapOf("currentMethod", "CONDOM")).map());

        verify(reportingService).updateFamilyPlanningMethod(new SafeMap(mapOf("currentMethod", "CONDOM")));
    }

    @Test
    public void shouldUpdateFPComplicationsScheduleAndCloseAlertsWhenFPMethodIsUpdated() throws Exception {
        Map<String, String> existingDetails = mapOf("existingThing", "existingValue");
        EligibleCouple existingCoupleBeforeUpdate = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(existingDetails);
        Map<String, String> updatedDetails = create("currentMethod", "CONDOM").put("existingThing", "existingValue").map();
        EligibleCouple existingCoupleAfterUpdate = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(updatedDetails);
        when(allEligibleCouples.findByCaseId("CASE X")).thenReturn(existingCoupleBeforeUpdate);
        when(allEligibleCouples.updateDetails("CASE X", mapOf("currentMethod", "CONDOM"))).thenReturn(existingCoupleAfterUpdate);
        Map<String, Map<String, String>> extraDetails = create("details", mapOf("currentMethod", "CONDOM")).put(REPORT_EXTRA_DATA_KEY_NAME, mapOf("currentMethod", "CONDOM")).map();
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", "ANM X").withCurrentMethod("CONDOM");

        ecService.updateFamilyPlanningMethod(request, extraDetails);

        verify(schedulingService).updateFPComplications(request, existingCoupleAfterUpdate);
    }

    @Test
    public void shouldCloseFPScheduleAlertsWhenCoupleStartedUsingFPMethod() throws Exception {
        Map<String, String> details = mapOf("existingThing", "existingValue");
        EligibleCouple ec = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(details);
        when(allEligibleCouples.findByCaseId("CASE X")).thenReturn(ec);
        when(allEligibleCouples.updateDetails("CASE X", mapOf("currentMethod", "CONDOM"))).thenReturn(ec);
        Map<String, Map<String, String>> extraDetails = create("details", mapOf("currentMethod", "CONDOM")).put(REPORT_EXTRA_DATA_KEY_NAME, mapOf("currentMethod", "CONDOM")).map();
        FamilyPlanningUpdateRequest request = new FamilyPlanningUpdateRequest("CASE X", "ANM X").withCurrentMethod("CONDOM").withFPStartDate("2012-01-01");

        ecService.updateFamilyPlanningMethod(request, extraDetails);

        verify(actionService).markAlertAsClosed("CASE X", "ANM X", EC_SCHEDULE_FP_COMPLICATION_MILESTONE, "2012-01-01");
    }

    @Test
    public void shouldNotUpdateDetailsIfECIsNotFoundWhenFPMethodIsUpdated() throws Exception {
        when(allEligibleCouples.findByCaseId("CASE X")).thenReturn(null);

        ecService.updateFamilyPlanningMethod(new FamilyPlanningUpdateRequest("CASE X", "ANM X"), mapOf("details", mapOf("currentMethod", "CONDOM")));

        verify(allEligibleCouples).findByCaseId("CASE X");
        verifyNoMoreInteractions(allEligibleCouples);
        verifyZeroInteractions(actionService);
    }

    @Test
    public void shouldCloseEligibleCouple() throws Exception {
        ecService.closeEligibleCouple(new EligibleCoupleCloseRequest("CASE X", "ANM X"));

        verify(allEligibleCouples).close("CASE X");
        verify(actionService).closeEligibleCouple("CASE X", "ANM X");
    }
}
