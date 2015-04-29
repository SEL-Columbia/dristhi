package org.opensrp.service;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.common.util.ANMIndicatorSummaryFactory.createSummaryForANC;
import static org.opensrp.common.util.ANMIndicatorSummaryFactory.createSummaryForIUD;
import static org.opensrp.dto.ActionData.reportForIndicator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.opensrp.common.domain.ANMReport;
import org.opensrp.dto.MonthSummaryDatum;
import org.opensrp.scheduler.service.ActionService;
import org.opensrp.service.reporting.ANMReportingService;
import org.opensrp.service.reporting.ChildReportingService;
import org.opensrp.service.reporting.MotherReportingService;

import com.google.gson.Gson;

public class ANMReportingServiceTest {
    @Mock
    private ActionService actionService;
    @Mock
    private ChildReportingService childReportingService;
    @Mock
    private MotherReportingService motherReportingService;

    private ANMReportingService anmReportingService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        anmReportingService = new ANMReportingService(actionService, motherReportingService, childReportingService);
    }

    @Test
    public void shouldCreateActionsForReports() throws Exception {
        List<ANMReport> anmReports = new ArrayList<>();
        anmReports.add(new ANMReport("ANM X", asList(createSummaryForIUD())));
        anmReports.add(new ANMReport("ANM Y", asList(createSummaryForANC())));

        anmReportingService.processReports(anmReports);

        InOrder inOrder = inOrder(actionService);
        inOrder.verify(actionService).deleteReportActions();
        inOrder.verify(actionService).reportForIndicator("ANM X",
                reportForIndicator("IUD", "40", new Gson().toJson(asList(new MonthSummaryDatum("4", "2012", "2", "2", asList("CASE 1", "CASE 2")),
                        new MonthSummaryDatum("5", "2012", "2", "4", asList("CASE 3", "CASE 4"))))));
        inOrder.verify(actionService).reportForIndicator("ANM Y",
                reportForIndicator("ANC", "30", new Gson().toJson(asList(new MonthSummaryDatum("6", "2012", "2", "2", asList("CASE 5", "CASE 6"))))));
    }

    @Test
    public void shouldReportEntityDataToServices() throws Exception {
        anmReportingService.reportFromEntityData();

        verify(childReportingService).reportInfantAndChildBalance();
    }
}
