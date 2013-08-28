package org.ei.drishti.controller;

import org.ei.commcare.listener.CommCareFormSubmissionRouter;
import org.ei.drishti.contract.BirthPlanningRequest;
import org.ei.drishti.service.ANCService;
import org.ei.drishti.service.ChildService;
import org.ei.drishti.service.PNCService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DrishtiControllerTest {
    public static final Map<String, Map<String, String>> EXTRA_DATA = create(REPORT_EXTRA_DATA_KEY_NAME, mapOf("Some", "Data")).put("details", mapOf("SomeOther", "PieceOfData")).map();
    @Mock
    private CommCareFormSubmissionRouter dispatcher;
    @Mock
    private ANCService ancService;
    @Mock
    private PNCService pncService;
    @Mock
    private ChildService childService;
    private DrishtiController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new DrishtiController(dispatcher, ancService);
    }

    @Test
    public void shouldDelegateToANCServiceDuringBirthPlanningUpdate() throws Exception {
        BirthPlanningRequest request = mock(BirthPlanningRequest.class);

        controller.updateBirthPlanning(request, EXTRA_DATA);

        verify(ancService).updateBirthPlanning(request, EXTRA_DATA);
    }
}
