package org.ei.drishti.reporting.controller;

import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.reporting.repository.AllServicesProvided;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReportDataControllerTest {
    @Mock
    private AllServicesProvided allServicesProvided;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldSaveImmunizationsInDB() throws Exception {
        ReportDataController controller = new ReportDataController(allServicesProvided);

        Map<String, String> data = new HashMap<>();
        data.put("thaayiCardNumber", "TC 1");
        data.put("immunizationsProvidedDate", "2012-01-01");
        data.put("immunizationsProvided", "bcg");
        data.put("anmIdentifier", "ANM X");

        controller.submit(new ReportingData("updateChildImmunization", data));

        verify(allServicesProvided).save("TC 1", new LocalDate(2012, 1, 1).toDate(), "bcg", "ANM X");
    }
}
