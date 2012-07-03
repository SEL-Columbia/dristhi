package org.ei.drishti.service;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class MotherReportingServiceTest extends BaseUnitTest{
    private MotherReportingService reportingService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mockCurrentDate(new LocalDate(2012, 1, 1));
    }

    @Test
    public void shouldReportANCRegistrationWhichHasBeenDoneWithinTwelveWeeksOfLMP() throws Exception {
        assertThatIndicatorIsSetBasedOnLMP("2011-10-11", "ANC<12");
        assertThatIndicatorIsSetBasedOnLMP("2011-10-10", "ANC<12");
    }

    @Test
    public void shouldReportANCRegistrationWhichHasBeenDoneAfterExactlyTwelveWeeksAfterLMPAsLessThanTwelve() throws Exception {
        assertThatIndicatorIsSetBasedOnLMP("2011-10-09", "ANC<12");
    }

    @Test
    public void shouldReportANCRegistrationWhichHasBeenDoneAfterTwelveWeeksAfterLMPAsGreaterThanTwelve() throws Exception {
        assertThatIndicatorIsSetBasedOnLMP("2011-10-08", "ANC>12");
        assertThatIndicatorIsSetBasedOnLMP("2011-10-07", "ANC>12");
        assertThatIndicatorIsSetBasedOnLMP("2011-10-01", "ANC>12");
    }

    private void assertThatIndicatorIsSetBasedOnLMP(String lmp, String indicator) {
        Map<String, String> reportData = setUpReportData(lmp);

        ReportingService reportingService = mock(ReportingService.class);
        MotherReportingService motherReportingService = new MotherReportingService(reportingService);
        motherReportingService.registerANC(reportData);

        ReportingData data = ReportingData.serviceProvidedData("ANM X", "TC 1", indicator, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        verify(reportingService).sendReportData(data);
    }

    private Map<String, String> setUpReportData(String lmp) {
        Map<String, String> reportData = new HashMap<>();
        reportData.put("anmIdentifier", "ANM X");
        reportData.put("thaayiCardNumber", "TC 1");
        reportData.put("lmp", lmp);
        reportData.put("village", "bherya");
        reportData.put("subCenter", "Sub Center");
        reportData.put("phc", "PHC X");
        return reportData;
    }
}
