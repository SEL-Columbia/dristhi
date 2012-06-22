package org.ei.drishti.reporting.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

public class ReportsRepositoryTest {
    @Mock
    private AllServicesProvided allServicesProvided;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveReportData() throws Exception {
        ReportsRepository repository = new ReportsRepository(allServicesProvided);
    }
}
