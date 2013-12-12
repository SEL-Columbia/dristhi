package org.ei.drishti.reporting.controller;

import org.ei.drishti.dto.LocationDTO;
import org.ei.drishti.dto.aggregatorResponse.AggregatorResponseDTO;
import org.ei.drishti.dto.report.AggregatedReportsDTO;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.domain.PHC;
import org.ei.drishti.reporting.service.ANMService;
import org.ei.drishti.reporting.service.AggregateReportsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AggregatedReportControllerTest {

    @Mock
    private AggregateReportsService aggregateReportsService;
    @Mock
    private ANMService anmService;

    private AggregatedReportController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new AggregatedReportController(aggregateReportsService, "http://dristhi_site_url", anmService);
    }

    @Test
    public void shouldReturnAggregatedReportDataForAGivenANMMonthAndYear() {
        when(aggregateReportsService.getAggregatedReports("demo1", 12, 2013)).thenReturn(asList(new AggregatorResponseDTO("OCP", 4), new AggregatorResponseDTO("OPV_1", 2)));
        when(anmService.getLocation("demo1")).thenReturn(new Location("Bherya", "sub Center", new PHC(1, "PHC 1", "Bherya"), "taluka", "mysore", "karnataka"));

        ResponseEntity<AggregatedReportsDTO> aggregatedReports = controller.get("demo1", 12, 2013);

        assertEquals(new AggregatedReportsDTO(create("OCP", 4).put("OPV_1", 2).map(), new LocationDTO("Sub Center", "Bherya", "taluka", "mysore", "karnataka")), aggregatedReports.getBody());
        assertEquals(HttpStatus.OK, aggregatedReports.getStatusCode());
        assertTrue(aggregatedReports.getHeaders().containsKey("Access-Control-Allow-Origin"));
        assertEquals("http://dristhi_site_url", aggregatedReports.getHeaders().getFirst("Access-Control-Allow-Origin"));
    }
}
