package org.opensrp.common.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by real on 10/07/17.
 */
public class ANMIndicatorSummaryTest {


    @Test
    public void testMonthlySummaries() {
        List<String> externalIDs = new ArrayList<>();
        externalIDs.add("1");
        MonthSummary monthSummary, monthSummary2;
        monthSummary = new MonthSummary("July", "2017", "73%",
                "47%", externalIDs);
        List<MonthSummary> monthSummaryList = new ArrayList<>();
        monthSummaryList.add(monthSummary);
        ANMIndicatorSummary anmIndicatorSummary = new ANMIndicatorSummary("11", "11111", monthSummaryList);
        assertEquals(monthSummaryList, anmIndicatorSummary.monthlySummaries());

        assertTrue(anmIndicatorSummary.toString().contains("indicator=11"));
        assertFalse(anmIndicatorSummary.toString().contains("annualTarget=2222"));

        assertEquals("11111", anmIndicatorSummary.annualTarget());
        assertNotSame("11112", anmIndicatorSummary.annualTarget());

        assertEquals("11", anmIndicatorSummary.indicator());
        assertNotSame("12", anmIndicatorSummary.indicator());

        monthSummary2 = new MonthSummary("august", "2015", "63%",
                "74%", externalIDs);
        List<MonthSummary> monthSummaryList2 = new ArrayList<>();
        monthSummaryList2.add(monthSummary2);
        ANMIndicatorSummary anmIndicatorSummary2 = new ANMIndicatorSummary("11", "11111", monthSummaryList);
        assertNotSame(monthSummaryList2, anmIndicatorSummary2.monthlySummaries());
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier.forClass(ANMIndicatorSummary.class).verify();
    }
}
