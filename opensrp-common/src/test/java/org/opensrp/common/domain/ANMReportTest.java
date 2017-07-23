package org.opensrp.common.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Created by real on 10/07/17.
 */
public class ANMReportTest {
    @Test
    public void testConstructorsAndGetters() {

        List<String> externalIDs = new ArrayList<>();
        externalIDs.add("1");
        MonthSummary monthSummary, monthSummary2;
        monthSummary = new MonthSummary("July", "2017", "73%",
                "47%", externalIDs);
        List<MonthSummary> monthSummaryList = new ArrayList<>();
        monthSummaryList.add(monthSummary);

        ANMIndicatorSummary anmIndicatorSummary, anmIndicatorSummary2;
        anmIndicatorSummary = new ANMIndicatorSummary("11", "1111", monthSummaryList);
        anmIndicatorSummary2 = new ANMIndicatorSummary("22", "2222", monthSummaryList);

        List<ANMIndicatorSummary> summuriesList = new ArrayList<>();
        summuriesList.add(anmIndicatorSummary);

        List<ANMIndicatorSummary> summuriesList2 = new ArrayList<>();
        summuriesList.add(anmIndicatorSummary2);

        ANMReport anmReport = new ANMReport("1234", summuriesList);
        assertEquals(summuriesList, anmReport.summaries());
        assertNotSame(summuriesList2, anmReport.summaries());

        assertEquals("1234", anmReport.anmIdentifier());
        assertNotSame("4321", anmReport.anmIdentifier());
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ANMReport.class).verify();
    }

    @Test
    public void testToString() {
        ANMReport anmReport = new ANMReport();
        assertNotSame("", anmReport.toString());

        List<String> externalIDs = new ArrayList<>();
        externalIDs.add("1");
        MonthSummary monthSummary, monthSummary2;
        monthSummary = new MonthSummary("July", "2017", "73%",
                "47%", externalIDs);
        List<MonthSummary> monthSummaryList = new ArrayList<>();
        monthSummaryList.add(monthSummary);
        ANMIndicatorSummary anmIndicatorSummary, anmIndicatorSummary2;
        anmIndicatorSummary = new ANMIndicatorSummary("11", "1111", monthSummaryList);
        List<ANMIndicatorSummary> summuriesList = new ArrayList<>();
        summuriesList.add(anmIndicatorSummary);
        ANMReport anmReport2 = new ANMReport("1234", summuriesList);
        assertEquals("1234", anmReport2.anmIdentifier().toString());

    }
}
