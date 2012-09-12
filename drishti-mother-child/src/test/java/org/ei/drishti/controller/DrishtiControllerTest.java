package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionRouter;
import org.ei.drishti.contract.*;
import org.ei.drishti.domain.EligibleCouple;
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

import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DrishtiControllerTest {
    public static final Map<String,Map<String,String>> EXTRA_DATA = create("reporting", mapOf("Some", "Data")).put("details", mapOf("SomeOther", "PieceOfData")).map();
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

        controller.registerMother(ancEnrollInfo, EXTRA_DATA);

        verify(ancService).registerANCCase(ancEnrollInfo, EXTRA_DATA);
        verify(mctsService).registerANCCase(ancEnrollInfo);
    }

    @Test
    public void shouldDelegateToBothANCServiceAndMCTSDuringANCCareProvision() throws Exception {
        AnteNatalCareInformation ancInformation = mock(AnteNatalCareInformation.class);

        controller.updateANCCareInformation(ancInformation, EXTRA_DATA);

        verify(ancService).ancCareHasBeenProvided(ancInformation, EXTRA_DATA);
        verify(mctsService).ancCareHasBeenProvided(ancInformation);
    }

    @Test
    public void shouldDelegateToBothANCServiceAndMCTSDuringANCCareOutcome() throws Exception {
        AnteNatalCareOutcomeInformation careOutcomeInformation = mock(AnteNatalCareOutcomeInformation.class);

        controller.updateOutcomeOfANC(careOutcomeInformation, EXTRA_DATA);

        verify(ancService).updatePregnancyOutcome(careOutcomeInformation, EXTRA_DATA);
        verify(mctsService).updateANCOutcome(careOutcomeInformation);
    }

    @Test
    public void shouldDelegateToBothANCServiceAndMCTSDuringANCClose() throws Exception {
        AnteNatalCareCloseInformation closeInformation = mock(AnteNatalCareCloseInformation.class);

        controller.closeANCCase(closeInformation, EXTRA_DATA);

        verify(ancService).closeANCCase(closeInformation, new SafeMap(EXTRA_DATA.get("reporting")));
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

        controller.updateChildImmunization(updationRequest, EXTRA_DATA);

        verify(pncService).updateChildImmunization(updationRequest, new SafeMap(EXTRA_DATA.get("reporting")));
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

        verify(ecService).registerEligibleCouple(eligibleCoupleRegistrationRequest, new HashMap<String, Map<String, String>>());
    }

    @Test
    public void shouldDelegateToECServiceDuringEligibleCoupleFamilyPlanningMethodChange() {
        UpdateDetailsRequest request = mock(UpdateDetailsRequest.class);

        controller.changeFamilyPlanningMethod(request, EXTRA_DATA);

        verify(ecService).updateDetails(request, EXTRA_DATA);
    }

    @Test
    public void shouldDelegateToECServiceDuringEligibleCoupleClose() {
        EligibleCoupleCloseRequest eligibleCoupleCloseRequest = mock(EligibleCoupleCloseRequest.class);

        controller.closeEligibleCouple(eligibleCoupleCloseRequest);

        verify(ecService).closeEligibleCouple(eligibleCoupleCloseRequest);
    }

    @Test
    public void shouldDelegateToECServiceAndANCServiceDuringOutOfAreaRegistration() {
        OutOfAreaANCRegistrationRequest request = mock(OutOfAreaANCRegistrationRequest.class);

        controller.registerOutOfAreaANC(request, EXTRA_DATA);

        verify(ecService).registerEligibleCoupleForOutOfAreaANC(request, EXTRA_DATA);
        verify(ancService).registerOutOfAreaANC(eq(request), any(EligibleCouple.class), eq(EXTRA_DATA));
    }
}
