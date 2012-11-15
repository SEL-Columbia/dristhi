package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.ei.drishti.util.EasyMap.create;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECReportingServiceTest {
    @Mock
    private ReportingService reportingService;

    private ECReportingService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ECReportingService(reportingService);
    }

    @Test
    public void shouldReportFPMethodChange() throws Exception {
        service.fpMethodChanged(new SafeMap(create("anmIdentifier", "ANM X")
                .put("ecNumber", "EC NUMBER 1")
                .put("currentMethod", "iud")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .map()));

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC NUMBER 1", Indicator.IUD, "2012-01-01"));
    }

    @Test
    public void shouldNotReportFPMethodChangeWhenNoIndicatorIsFoundForTheCurrentFPMethod() throws Exception {
        service.fpMethodChanged(new SafeMap(create("anmIdentifier", "ANM X")
                .put("ecNumber", "EC NUMBER 1")
                .put("currentMethod", "none")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .map()));

        verifyZeroInteractions(reportingService);
    }
}
