package org.ei.drishti.service;

import org.ei.drishti.contract.EligibleCoupleCloseRequest;
import org.ei.drishti.contract.EligibleCoupleRegistrationRequest;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
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
        ecService.registerEligibleCouple(new EligibleCoupleRegistrationRequest("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X"));

        verify(allEligibleCouples).register(new EligibleCouple("CASE X", "EC Number 1").withCouple("Wife 1", "Husband 1").withANMIdentifier("ANM X"));
        verify(actionService).registerEligibleCouple("CASE X", "EC Number 1", "Wife 1", "Husband 1", "ANM X");
    }

    @Test
    public void shouldCloseEligibleCouple() throws Exception {
        ecService.closeEligibleCouple(new EligibleCoupleCloseRequest("CASE X", "ANM X"));

        verify(allEligibleCouples).close("CASE X");
        verify(actionService).closeEligibleCouple("CASE X", "ANM X");
    }
}
