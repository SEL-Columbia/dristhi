package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.ei.drishti.util.EasyMap.create;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECReportingServiceTest {
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllEligibleCouples allEligibleCouples;

    private ECReportingService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ECReportingService(reportingService, allEligibleCouples);
    }

    @Test
    public void shouldReportFPMethodChangeDuringRegisterEC() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_IUD, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_IUD, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldReportFPMethodChange() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("method_still_the_same", "no")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_IUD, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_IUD, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldNotReportFPMethodChangeWhenNoIndicatorIsFoundForTheCurrentFPMethod() throws Exception {
        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "none")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .map());

        service.registerEC(reportData);

        verifyZeroInteractions(reportingService);
    }
}
