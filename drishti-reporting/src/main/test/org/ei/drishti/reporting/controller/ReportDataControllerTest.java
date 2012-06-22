package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.Location;
import org.ei.drishti.reporting.repository.ReportsRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.ei.drishti.common.domain.ReportingData.updateChildImmunization;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportDataControllerTest {
    @Mock
    private ReportsRepository reportsRepository;
    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveImmunizationsInDB() throws Exception {
        ReportDataController controller = new ReportDataController(reportsRepository);
        ReportingData data = updateChildImmunization("ANM X", "TC 1", "bcg", "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));

        controller.submit(data);

        verify(reportsRepository).save("ANM X", "TC 1", "bcg", "2012-01-01", "bherya", "Sub Center", "PHC X");
    }
}
