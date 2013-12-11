package org.ei.drishti.reporting.controller;

import org.ei.drishti.dto.aggregatorResponse.AggregatorResponseDTO;
import org.ei.drishti.dto.report.AggregatedReportsDTO;
import org.ei.drishti.reporting.service.AggregateReportsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AggregatedReportControllerTest {

    @Mock
    private AggregateReportsService aggregateReportsService;
    private AggregatedReportController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new AggregatedReportController(aggregateReportsService);
    }

    @Test
    public void shouldReturnAggregatedReportDataForAGivenANMMonthAndYear() {
        when(aggregateReportsService.getAggregatedReports("demo1", 12, 2013)).thenReturn(asList(new AggregatorResponseDTO("OCP", 4), new AggregatorResponseDTO("OPV_1", 2)));

        AggregatedReportsDTO aggregatedReports = controller.get("demo1", 12, 2013);

        assertEquals(new AggregatedReportsDTO(create("OCP", 4).put("OPV_1", 2).map()), aggregatedReports);
    }

}
