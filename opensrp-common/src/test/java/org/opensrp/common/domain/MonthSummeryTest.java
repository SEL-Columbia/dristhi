package org.opensrp.common.domain;


import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.opensrp.common.audit.AuditMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MonthSummeryTest {

    @Test
    public void testForConstructorAndGettersOfMonthSummery() {
        List<String> externalIDs = new ArrayList<>();
        externalIDs.add("hi");
        externalIDs.add("hello");
        MonthSummary monthSummary, monthSummary2, monthSummary3;
        monthSummary = new MonthSummary("July", "2017", "73%",
                "47%", externalIDs);
        monthSummary2 = new MonthSummary("august", "2018", "63%",
                "37%", externalIDs);
        monthSummary3 = monthSummary;

        assertEquals("July", monthSummary.month());
        assertNotSame("June", monthSummary.month());

        assertEquals("2017", monthSummary.year());
        assertNotSame("2018", monthSummary.year());

        assertEquals("73%", monthSummary.currentProgress());
        assertNotSame("83%", monthSummary.currentProgress());

        assertEquals("47%", monthSummary.aggregatedProgress());
        assertNotSame("74%", monthSummary.aggregatedProgress());

        assertEquals(externalIDs, monthSummary.externalIDs());
        assertNotSame("hi", monthSummary.externalIDs().get(1));

        assertEquals(monthSummary.toString(), monthSummary3.toString());
        assertNotSame(monthSummary.toString(), monthSummary2.toString());

    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(MonthSummary.class)
                .verify();
    }

}
