package org.ei.drishti.service;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllChildren;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildReportingServiceTest {
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllChildren allChildren;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSendChildReportingData() throws Exception {
        ChildReportingService service = new ChildReportingService(reportingService, allChildren);

        Map<String, String> reportingData = new HashMap<>();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "TC 1", "boo", Arrays.asList("bcg", "hep")).withLocation("bherya", "Sub Center", "PHC X"));

        service.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", "bcg hep opv"), reportingData);

        ReportingData expectedReportingData = ReportingData.updateChildImmunization("ANM X", "TC 1", "opv", "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        verify(reportingService).sendReportData(expectedReportingData);
    }

    @Test
    public void shouldNotSendChildReportingDataWhenChildIsNotFound() throws Exception {
        ChildReportingService service = new ChildReportingService(reportingService, allChildren);

        Map<String, String> reportingData = new HashMap<>();
        reportingData.put("anmIdentifier", "ANM X");
        when(allChildren.findByCaseId("CASE X")).thenReturn(null);

        service.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", "bcg hep opv"), reportingData);

        verifyZeroInteractions(reportingService);
    }
}
