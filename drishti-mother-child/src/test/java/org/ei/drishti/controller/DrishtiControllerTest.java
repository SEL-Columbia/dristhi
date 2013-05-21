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

import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DrishtiControllerTest {
    public static final Map<String, Map<String, String>> EXTRA_DATA = create(REPORT_EXTRA_DATA_KEY_NAME, mapOf("Some", "Data")).put("details", mapOf("SomeOther", "PieceOfData")).map();
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
    @Mock
    private ChildMapper childMapper;

    private DrishtiController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new DrishtiController(dispatcher, ancService, pncService, ecService, mctsService, childMapper);
    }

    @Test
    public void shouldDelegateToBothANCServiceAndMCTSDuringANCCareProvision() throws Exception {
        AnteNatalCareInformation ancInformation = mock(AnteNatalCareInformation.class);

        controller.updateANCCareInformation(ancInformation, EXTRA_DATA);

        verify(ancService).ancHasBeenProvided(ancInformation, EXTRA_DATA);
        verify(mctsService).ancCareHasBeenProvided(ancInformation);
    }

    @Test
    public void shouldDelegateToBothANCServiceAndMCTSDuringANCCareOutcome() throws Exception {
        AnteNatalCareOutcomeInformation outcomeInformation = mock(AnteNatalCareOutcomeInformation.class);
        ChildInformation childInformation = mock(ChildInformation.class);
        when(childMapper.mapDeliveryOutcomeInformationToChildren(outcomeInformation, EXTRA_DATA)).thenReturn(asList(childInformation));

        controller.updateOutcomeOfANC(outcomeInformation, EXTRA_DATA);

        verify(ancService).updatePregnancyOutcome(outcomeInformation, EXTRA_DATA);
        verify(pncService).registerPNC(outcomeInformation);
        verify(mctsService).updateANCOutcome(outcomeInformation);
        verify(mctsService).registerChild(outcomeInformation);
    }

    @Test
    public void shouldDelegateToChildMapperBeforeRegisteringChildDuringANCCareOutcome() throws Exception {
        AnteNatalCareOutcomeInformation outcomeInformation = mock(AnteNatalCareOutcomeInformation.class);
        ChildInformation firstChildInformation = mock(ChildInformation.class);
        ChildInformation secondChildInformation = mock(ChildInformation.class);
        when(childMapper.mapDeliveryOutcomeInformationToChildren(outcomeInformation, EXTRA_DATA)).thenReturn(asList(firstChildInformation, secondChildInformation));

        controller.updateOutcomeOfANC(outcomeInformation, EXTRA_DATA);

        verify(childMapper).mapDeliveryOutcomeInformationToChildren(outcomeInformation, EXTRA_DATA);
        verify(pncService).registerChild(firstChildInformation);
        verify(pncService).registerChild(secondChildInformation);
    }

    @Test
    public void shouldDelegateToBothANCServiceAndMCTSDuringANCClose() throws Exception {
        AnteNatalCareCloseInformation closeInformation = mock(AnteNatalCareCloseInformation.class);

        controller.closeANCCase(closeInformation, EXTRA_DATA);

        verify(ancService).closeANCCase(closeInformation, new SafeMap(EXTRA_DATA.get(REPORT_EXTRA_DATA_KEY_NAME)));
        verify(mctsService).closeANCCase(closeInformation);
    }

    @Test
    public void shouldDelegateToBothPNCServiceAndMCTSDuringChildImmunizationUpdation() {
        ChildImmunizationUpdationRequest updationRequest = mock(ChildImmunizationUpdationRequest.class);

        controller.updateChildImmunization(updationRequest, EXTRA_DATA);

        verify(pncService).updateChildImmunization(updationRequest, EXTRA_DATA);
        verify(mctsService).updateChildImmunization(updationRequest);
    }

    @Test
    public void shouldDelegateToBothPNCServiceAndMCTSDuringChildCaseClose() {
        ChildCloseRequest childCloseRequest = mock(ChildCloseRequest.class);

        controller.closeChildCase(childCloseRequest, EXTRA_DATA);

        verify(pncService).closeChildCase(childCloseRequest, EXTRA_DATA);
        verify(mctsService).closeChildCase(childCloseRequest);
    }

    @Test
    public void shouldDelegateToBothPNCServiceAndMCTSDuringPNCCaseClose() {
        PostNatalCareCloseInformation closeInformation = mock(PostNatalCareCloseInformation.class);

        controller.closePNCCase(closeInformation, EXTRA_DATA);

        verify(pncService).closePNCCase(closeInformation, EXTRA_DATA);
        verify(mctsService).closePNCCase(closeInformation);
    }

    @Test
    public void shouldDelegateToPNCServiceDuringPNCAndChildCareUpdate() throws Exception {
        PostNatalCareInformation request = mock(PostNatalCareInformation.class);

        controller.updatePNCAndChildInformation(request, EXTRA_DATA);

        verify(pncService).pncVisitHappened(request, EXTRA_DATA);
    }

    @Test
    public void shouldDelegateToANCServiceDuringBirthPlanningUpdate() throws Exception {
        BirthPlanningRequest request = mock(BirthPlanningRequest.class);

        controller.updateBirthPlanning(request, EXTRA_DATA);

        verify(ancService).updateBirthPlanning(request, EXTRA_DATA);
    }

    @Test
    public void shouldDelegateToANCServiceDuringANCSubsetUpdate() throws Exception {
        AnteNatalCareInformationSubset request = mock(AnteNatalCareInformationSubset.class);

        controller.updateSubsetOfANCInformation(request, EXTRA_DATA);

        verify(ancService).updateSubsetOfANCInformation(request, EXTRA_DATA);
    }
}
