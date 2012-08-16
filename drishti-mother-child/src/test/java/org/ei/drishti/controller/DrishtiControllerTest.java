package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionRouter;
import org.ei.drishti.contract.*;
import org.ei.drishti.service.ANCService;
import org.ei.drishti.service.DrishtiMCTSService;
import org.ei.drishti.service.ECService;
import org.ei.drishti.service.PNCService;
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

        HashMap<String, Map<String, String>> extraData = new HashMap<>();
        HashMap<String, String> reportingData = new HashMap<>();
        reportingData.put("Some", "Data");
        extraData.put("reporting", reportingData);
        controller.registerMother(ancEnrollInfo, extraData);

        verify(ancService).registerANCCase(ancEnrollInfo, new SafeMap(reportingData));
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

        HashMap<String, Map<String, String>> extraData = new HashMap<>();
        HashMap<String, String> reportingData = new HashMap<>();
        reportingData.put("Some", "Data");
        extraData.put("reporting", reportingData);
        controller.closeANCCase(closeInformation, extraData);

        verify(ancService).closeANCCase(closeInformation, new SafeMap(reportingData));
        verify(mctsService).closeANCCase(closeInformation);
    }

    @Test
    public void shouldDelegateToBothPNCServiceAndMCTSDuringNewChildRegistration() {
        ChildRegistrationRequest childRegistrationRequest = mock(ChildRegistrationRequest.class);

        controller.registerChild(childRegistrationRequest);

        verify(pncService).registerChild(childRegistrationRequest);
        verify(mctsService).registerChild(childRegistrationRequest);
    }

    @Test
    public void shouldDelegateToBothPNCServiceAndMCTSDuringChildImmunizationUpdation() {
        ChildImmunizationUpdationRequest updationRequest = mock(ChildImmunizationUpdationRequest.class);

        HashMap<String, Map<String, String>> extraData = new HashMap<>();
        HashMap<String, String> reportingData = new HashMap<>();
        reportingData.put("Some", "Data");
        extraData.put("reporting", reportingData);
        controller.updateChildImmunization(updationRequest, extraData);

        verify(pncService).updateChildImmunization(updationRequest, new SafeMap(reportingData));
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

        controller.registerEligibleCouple(eligibleCoupleRegistrationRequest, new HashMap<String, Map<String, String>>());

        verify(ecService).registerEligibleCouple(eligibleCoupleRegistrationRequest);
    }

    @Test
    public void shouldDelegateToECServiceDuringEligibleCoupleClose() {
        EligibleCoupleCloseRequest eligibleCoupleCloseRequest = mock(EligibleCoupleCloseRequest.class);

        controller.closeEligibleCouple(eligibleCoupleCloseRequest);

        verify(ecService).closeEligibleCouple(eligibleCoupleCloseRequest);
    }
}
