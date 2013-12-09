package org.ei.drishti.reporting.controller;

import org.ei.drishti.dto.aggregatorResponse.AggregatorResponseDTO;
import org.ei.drishti.dto.report.AggregatedReportsDTO;
import org.ei.drishti.reporting.service.AggregateReportsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.ei.drishti.common.util.EasyMap.mapOf;
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
        when(aggregateReportsService.getAggregatedReports("demo1", 12, 2013)).thenReturn(new AggregatorResponseDTO(mapOf("OCP", 4)));

        AggregatedReportsDTO aggregatedReports = controller.get("demo1", 12, 2013);

        assertEquals(new AggregatedReportsDTO(mapOf("OCP", 4)), aggregatedReports);
    }

}
