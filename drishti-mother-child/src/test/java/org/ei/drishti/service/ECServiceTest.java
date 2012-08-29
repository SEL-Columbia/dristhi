package org.ei.drishti.service;

import org.ei.drishti.contract.EligibleCoupleCloseRequest;
import org.ei.drishti.contract.EligibleCoupleRegistrationRequest;
import org.ei.drishti.contract.UpdateDetailsRequest;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Map;

import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECServiceTest {
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private ActionService actionService;

    private ECService ecService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ecService = new ECService(allEligibleCouples, actionService);
    }

    @Test
    public void shouldRegisterEligibleCouple() throws Exception {
        Map<String, Map<String, String>> extraData = mapOf("details", Collections.<String, String>emptyMap());
        ecService.registerEligibleCouple(new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X"), extraData);

        EligibleCouple couple = new EligibleCouple("CASE X", "EC Number 1").withCouple("Wife 1", "Husband 1")
                .withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(extraData.get("details"));
        verify(allEligibleCouples).register(couple);
        verify(actionService).registerEligibleCouple("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X", "Village X", "SubCenter X", "PHC X", extraData.get("details"));
    }

    @Test
    public void shouldUpdateExistingDetailsBlobInECAndCreateAnActionForAnUpdateDetailsCall() throws Exception {
        Map<String, String> updatedDetails = create("currentMethod", "CONDOM").put("existingThing", "existingValue").map();
        EligibleCouple existingCoupleAfterUpdate = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("ANM X").withLocation("Village X", "SubCenter X", "PHC X").withDetails(updatedDetails);

        when(allEligibleCouples.findByCaseId("CASE X")).thenReturn(existingCoupleAfterUpdate);

        ecService.updateDetails(new UpdateDetailsRequest("CASE X", "ANM X"), mapOf("details", mapOf("currentMethod", "CONDOM")));

        verify(allEligibleCouples).updateDetails("CASE X", mapOf("currentMethod", "CONDOM"));
        verify(allEligibleCouples).findByCaseId("CASE X");
        verify(actionService).updateEligibleCoupleDetails("CASE X", "ANM X", updatedDetails);
    }

    @Test
    public void shouldCloseEligibleCouple() throws Exception {
        ecService.closeEligibleCouple(new EligibleCoupleCloseRequest("CASE X", "ANM X"));

        verify(allEligibleCouples).close("CASE X");
        verify(actionService).closeEligibleCouple("CASE X", "ANM X");
    }

}
