package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.reporting.repository.ANMReportsRepository;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.ei.drishti.common.domain.Indicator.BCG;
import static org.ei.drishti.common.domain.Indicator.IUD;
import static org.ei.drishti.common.domain.ReportingData.anmReportData;
import static org.ei.drishti.common.domain.ReportingData.serviceProvidedData;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportDataControllerTest {
    @Mock
    private ServicesProvidedRepository servicesProvidedRepository;
    @Mock
    private ANMReportsRepository anmReportsRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveServiceProvidedInDB() throws Exception {
        ReportDataController controller = new ReportDataController(servicesProvidedRepository, anmReportsRepository);
        ReportingData data = serviceProvidedData("ANM X", "TC 1", BCG, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));

        controller.submit(data);

        verify(servicesProvidedRepository).save("ANM X", "TC 1", "BCG", "2012-01-01", "bherya", "Sub Center", "PHC X");
    }

    @Test
    public void shouldSaveANMReportDataInDB() throws Exception {
        ReportDataController controller = new ReportDataController(servicesProvidedRepository, anmReportsRepository);
        ReportingData data = anmReportData("ANM X", "EC Number 1", IUD, "2012-01-01");

        controller.submit(data);

        verify(anmReportsRepository).save("ANM X", "EC Number 1", "IUD", "2012-01-01");
    }
}
