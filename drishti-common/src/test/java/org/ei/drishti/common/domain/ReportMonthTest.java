package org.ei.drishti.common.domain;

import org.ei.drishti.common.util.DateUtil;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReportMonthTest {

    private final ReportMonth reportMonth = new ReportMonth();

    @Test
    public void shouldCalculateStartOfCurrentReportMonth() throws Exception {
        assertEquals(LocalDate.parse("2011-11-26"), reportMonth.startOfCurrentReportMonth(LocalDate.parse("2011-11-28")));
        assertEquals(LocalDate.parse("2011-12-26"), reportMonth.startOfCurrentReportMonth(LocalDate.parse("2011-12-26")));
        assertEquals(LocalDate.parse("2011-12-26"), reportMonth.startOfCurrentReportMonth(LocalDate.parse("2011-12-27")));
        assertEquals(LocalDate.parse("2011-12-26"), reportMonth.startOfCurrentReportMonth(LocalDate.parse("2012-01-01")));

        assertEquals(LocalDate.parse("2012-01-26"), reportMonth.startOfCurrentReportMonth(LocalDate.parse("2012-01-26")));
        assertEquals(LocalDate.parse("2012-01-26"), reportMonth.startOfCurrentReportMonth(LocalDate.parse("2012-01-27")));

        assertEquals(LocalDate.parse("2012-01-26"), reportMonth.startOfCurrentReportMonth(LocalDate.parse("2012-02-01")));
        assertEquals(LocalDate.parse("2012-03-26"), reportMonth.startOfCurrentReportMonth(LocalDate.parse("2012-03-31")));

        assertEquals(LocalDate.parse("2012-01-26"), reportMonth.startOfCurrentReportMonth(LocalDate.parse("2012-02-25")));

        assertEquals(LocalDate.parse("2012-02-26"), reportMonth.startOfCurrentReportMonth(LocalDate.parse("2012-02-26")));
        assertEquals(LocalDate.parse("2012-02-26"), reportMonth.startOfCurrentReportMonth(LocalDate.parse("2012-02-27")));
    }

    @Test
    public void shouldCalculateStartOfNextReportMonth() throws Exception {
        assertEquals(LocalDate.parse("2012-01-26"), reportMonth.startDateOfNextReportingMonth(LocalDate.parse("2012-01-01")));
        assertEquals(LocalDate.parse("2012-02-26"), reportMonth.startDateOfNextReportingMonth(LocalDate.parse("2012-01-26")));
        assertEquals(LocalDate.parse("2012-02-26"), reportMonth.startDateOfNextReportingMonth(LocalDate.parse("2012-01-27")));

        assertEquals(LocalDate.parse("2011-12-26"), reportMonth.startDateOfNextReportingMonth(LocalDate.parse("2011-11-28")));

        assertEquals(LocalDate.parse("2011-12-26"), reportMonth.startDateOfNextReportingMonth(LocalDate.parse("2011-12-25")));
        assertEquals(LocalDate.parse("2012-01-26"), reportMonth.startDateOfNextReportingMonth(LocalDate.parse("2011-12-26")));
        assertEquals(LocalDate.parse("2012-01-26"), reportMonth.startDateOfNextReportingMonth(LocalDate.parse("2011-12-27")));
    }

    @Test
    public void shouldCalculateEndDateOfReportMonth() throws Exception {
        assertEquals(LocalDate.parse("2012-02-25"), reportMonth.endDateOfReportingMonthGivenStartDate(LocalDate.parse("2012-01-26")));
        assertEquals(LocalDate.parse("2012-12-25"), reportMonth.endDateOfReportingMonthGivenStartDate(LocalDate.parse("2012-11-26")));
        assertEquals(LocalDate.parse("2013-01-25"), reportMonth.endDateOfReportingMonthGivenStartDate(LocalDate.parse("2012-12-26")));
    }

    @Test
    public void shouldCalculateEndDateOfCurrentReportMonth() throws Exception {
        assertEquals(LocalDate.parse("2013-01-25"), reportMonth.endOfCurrentReportMonth(LocalDate.parse("2012-12-26")));
        assertEquals(LocalDate.parse("2013-01-25"), reportMonth.endOfCurrentReportMonth(LocalDate.parse("2012-12-30")));

        assertEquals(LocalDate.parse("2013-01-25"), reportMonth.endOfCurrentReportMonth(LocalDate.parse("2013-01-01")));

        assertEquals(LocalDate.parse("2013-02-25"), reportMonth.endOfCurrentReportMonth(LocalDate.parse("2013-01-26")));
        assertEquals(LocalDate.parse("2013-01-25"), reportMonth.endOfCurrentReportMonth(LocalDate.parse("2013-01-25")));
    }

    @Test
    public void shouldCheckWhetherTheDateIsWithInTheReportingMonth() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2013-01-26"));
        assertTrue(reportMonth.isDateWithinCurrentReportMonth(LocalDate.parse("2013-01-26")));
        assertTrue(reportMonth.isDateWithinCurrentReportMonth(LocalDate.parse("2013-02-01")));
        assertTrue(reportMonth.isDateWithinCurrentReportMonth(LocalDate.parse("2013-02-25")));
        assertFalse(reportMonth.isDateWithinCurrentReportMonth(LocalDate.parse("2013-01-25")));
        assertFalse(reportMonth.isDateWithinCurrentReportMonth(LocalDate.parse("2013-02-26")));

        DateUtil.fakeIt(LocalDate.parse("2012-12-26"));
        assertTrue(reportMonth.isDateWithinCurrentReportMonth(LocalDate.parse("2013-01-01")));
        assertTrue(reportMonth.isDateWithinCurrentReportMonth(LocalDate.parse("2013-01-25")));
    }
}

