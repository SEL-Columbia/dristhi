package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionRouter;
import org.ei.drishti.contract.*;
import org.ei.drishti.service.*;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DrishtiControllerTest {
    @Mock
    private CommCareFormSubmissionRouter dispatcher;
    @Mock
    private ANCService ancService;
    @Mock
    private ECService ecService;
    @Mock
    private DrishtiMCTSService mctsService;
    @Mock
    private PNCService pncService;

    private DrishtiController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new DrishtiController(dispatcher, ancService, pncService, ecService, mctsService);
    }

    @Test
    public void shouldDelegateToBothANCServiceAndMCTSDuringMotherRegistrationAndReportItAsWell() {
        AnteNatalCareEnrollmentInformation ancEnrollInfo = mock(AnteNatalCareEnrollmentInformation.class);

        SafeMap data = new SafeMap();
        controller.registerMother(ancEnrollInfo, data);

        verify(ancService).registerANCCase(ancEnrollInfo, data);
        verify(mctsService).registerANCCase(ancEnrollInfo);
    }

    @Test
    public void shouldDelegateToBothANCServiceAndMCTSDuringANCCareProvision() throws Exception {
        AnteNatalCareInformation ancInformation = mock(AnteNatalCareInformation.class);

        controller.updateANCCareInformation(ancInformation);

        verify(ancService).ancCareHasBeenProvided(ancInformation);
        verify(mctsService).ancCareHasBeenProvided(ancInformation);
    }

    @Test
    public void shouldDelegateToBothANCServiceAndMCTSDuringANCCareOutcome() throws Exception {
        AnteNatalCareOutcomeInformation careOutcomeInformation = mock(AnteNatalCareOutcomeInformation.class);

        controller.updateOutcomeOfANC(careOutcomeInformation);

        verify(ancService).updatePregnancyOutcome(careOutcomeInformation);
        verify(mctsService).updateANCOutcome(careOutcomeInformation);
    }

    @Test
    public void shouldDelegateToBothANCServiceAndMCTSDuringANCClose() throws Exception {
        AnteNatalCareCloseInformation closeInformation = mock(AnteNatalCareCloseInformation.class);

        SafeMap data = new SafeMap();
        controller.closeANCCase(closeInformation, data);

        verify(ancService).closeANCCase(closeInformation, data);
        verify(mctsService).closeANCCase(closeInformation);
    }

    @Test
    public void shouldDelegateToBothPNCServiceAndMCTSDuringNewChildRegistration() {
        ChildRegistrationRequest childRegistrationRequest = mock(ChildRegistrationRequest.class);

        Map<String, String> data = new HashMap<>();controller.registerChild(childRegistrationRequest);

        verify(pncService).registerChild(childRegistrationRequest);
        verify(mctsService).registerChild(childRegistrationRequest);
    }

    @Test
    public void shouldDelegateToBothPNCServiceAndMCTSDuringChildImmunizationUpdation() {
        ChildImmunizationUpdationRequest updationRequest = mock(ChildImmunizationUpdationRequest.class);

        SafeMap data = new SafeMap();
        controller.updateChildImmunization(updationRequest, data);

        verify(pncService).updateChildImmunization(updationRequest, data);
        verify(mctsService).updateChildImmunization(updationRequest);
    }

    @Test
    public void shouldDelegateToBothPNCServiceAndMCTSDuringChildCaseClose() {
        ChildCloseRequest childCloseRequest = mock(ChildCloseRequest.class);

        controller.closeChildCase(childCloseRequest);

        verify(pncService).closeChildCase(childCloseRequest);
        verify(mctsService).closeChildCase(childCloseRequest);
    }

    @Test
    public void shouldDelegateToECServiceDuringEligibleCoupleRegistration() {
        EligibleCoupleRegistrationRequest eligibleCoupleRegistrationRequest = mock(EligibleCoupleRegistrationRequest.class);

        controller.registerEligibleCouple(eligibleCoupleRegistrationRequest, new SafeMap());

        verify(ecService).registerEligibleCouple(eligibleCoupleRegistrationRequest);
    }

    @Test
    public void shouldDelegateToECServiceDuringEligibleCoupleClose() {
        EligibleCoupleCloseRequest eligibleCoupleCloseRequest = mock(EligibleCoupleCloseRequest.class);

        controller.closeEligibleCouple(eligibleCoupleCloseRequest);

        verify(ecService).closeEligibleCouple(eligibleCoupleCloseRequest);
    }
}
