package org.ei.drishti.service;

import org.ei.drishti.contract.EligibleCoupleCloseRequest;
import org.ei.drishti.contract.EligibleCoupleRegistrationRequest;
import org.ei.drishti.contract.OutOfAreaANCRegistrationRequest;
import org.ei.drishti.contract.UpdateDetailsRequest;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.service.reporting.ECReportingService;
import org.ei.drishti.util.IdGenerator;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_MAPS_KEY_NAME;
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

    private ECService ecService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ecService = new ECService(allEligibleCouples, actionService, reportingService, idGenerator);
    }

    @Test
    public void shouldRegisterEligibleCouple() throws Exception {
        Map<String, Map<String, String>> extraData = create("details", Collections.<String, String>emptyMap()).put(REPORT_EXTRA_MAPS_KEY_NAME, mapOf("someKey", "someValue")).map();

        ecService.registerEligibleCouple(new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X"), extraData);

        EligibleCouple expectedCouple = new EligibleCouple("CASE X", "EC Number 1").withCouple("Wife 1", "Husband 1")
                .withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(extraData.get("details"));
        verify(allEligibleCouples).register(expectedCouple);
        verify(reportingService).fpMethodChanged(new SafeMap(extraData.get(REPORT_EXTRA_MAPS_KEY_NAME)));
        verify(actionService).registerEligibleCouple("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", extraData.get("details"));
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
    public void shouldUpdateExistingDetailsBlobInECAndCreateAnActionForAnUpdateDetailsCall() throws Exception {
        Map<String, String> existingDetails = mapOf("existingThing", "existingValue");
        EligibleCouple existingCoupleBeforeUpdate = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(existingDetails);

        Map<String, String> updatedDetails = create("currentMethod", "CONDOM").put("existingThing", "existingValue").map();
        EligibleCouple existingCoupleAfterUpdate = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(updatedDetails);

        when(allEligibleCouples.findByCaseId("CASE X")).thenReturn(existingCoupleBeforeUpdate);
        when(allEligibleCouples.updateDetails("CASE X", mapOf("currentMethod", "CONDOM"))).thenReturn(existingCoupleAfterUpdate);

        ecService.updateDetails(new UpdateDetailsRequest("CASE X", "ANM X"), mapOf("details", mapOf("currentMethod", "CONDOM")));

        verify(allEligibleCouples).updateDetails("CASE X", mapOf("currentMethod", "CONDOM"));
        verify(allEligibleCouples).findByCaseId("CASE X");
        verify(actionService).updateEligibleCoupleDetails("CASE X", "ANM X", updatedDetails);
    }

    @Test
    public void shouldReportFPMethodChangeWhenDetailsAreUpdated() throws Exception {
        Map<String, String> existingDetails = mapOf("existingThing", "existingValue");
        EligibleCouple existingCoupleBeforeUpdate = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(existingDetails);
        Map<String, String> updatedDetails = create("currentMethod", "CONDOM").put("existingThing", "existingValue").map();
        EligibleCouple existingCoupleAfterUpdate = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(updatedDetails);
        when(allEligibleCouples.findByCaseId("CASE X")).thenReturn(existingCoupleBeforeUpdate);
        when(allEligibleCouples.updateDetails("CASE X", mapOf("currentMethod", "CONDOM"))).thenReturn(existingCoupleAfterUpdate);

        ecService.updateDetails(new UpdateDetailsRequest("CASE X", "ANM X"), create("details", mapOf("currentMethod", "CONDOM")).put(REPORT_EXTRA_MAPS_KEY_NAME, mapOf("currentMethod", "CONDOM")).map());

        verify(reportingService).fpMethodChanged(new SafeMap(mapOf("currentMethod", "CONDOM")));
    }

    @Test
    public void shouldIgnoreUpdationIfAnECIsNotFound() throws Exception {
        when(allEligibleCouples.findByCaseId("CASE X")).thenReturn(null);

        ecService.updateDetails(new UpdateDetailsRequest("CASE X", "ANM X"), mapOf("details", mapOf("currentMethod", "CONDOM")));

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
