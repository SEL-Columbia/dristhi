package org.ei.drishti.common.domain;

import org.ei.drishti.common.util.DateUtil;
import org.joda.time.LocalDate;

import java.util.Date;

import static org.ei.drishti.common.AllConstants.Report.*;

public class ReportMonth {

    private static final int JANUARY = 1;
    private static final int DECEMBER = 12;

    public Date startDateOfReportingYear() {
        LocalDate now = DateUtil.today();
        LocalDate beginningOfReportingYear = DateUtil.today().withMonthOfYear(REPORTING_MONTH).withDayOfMonth(REPORTING_MONTH_START_DAY);
        int reportingYear = now.isBefore(beginningOfReportingYear) ? previousYear(now) : now.getYear();
        return new LocalDate().withDayOfMonth(REPORTING_MONTH_START_DAY).withMonthOfYear(REPORTING_MONTH).withYear(reportingYear).toDate();
    }

    public LocalDate startDateOfNextReportingMonth() {
        LocalDate today = DateUtil.today();
        if (today.getDayOfMonth() < REPORTING_MONTH_START_DAY) {
            return new LocalDate(today.getYear(), today.getMonthOfYear(), REPORTING_MONTH_START_DAY);
        }
        return new LocalDate(today.getYear(), today.getMonthOfYear() + 1, REPORTING_MONTH_START_DAY);
    }

    public LocalDate endDateOfReportingMonth(LocalDate date) {
        return date.plusMonths(1).minusDays(1);
    }

    public LocalDate startOfCurrentReportMonth(LocalDate date) {
        if (date.getDayOfMonth() >= REPORTING_MONTH_START_DAY) {
            return new LocalDate(date.getYear(), date.getMonthOfYear(), REPORTING_MONTH_START_DAY);
        }
        if (date.getMonthOfYear() == JANUARY) {
            return new LocalDate(previousYear(date), DECEMBER, REPORTING_MONTH_START_DAY);
        }
        return new LocalDate(date.getYear(), previousMonth(date), REPORTING_MONTH_START_DAY);

    }

    public LocalDate endOfCurrentReportMonth(LocalDate date) {
        if (date.getDayOfMonth() > REPORTING_MONTH_END_DAY) {
            if (date.getMonthOfYear() == DECEMBER) {
                return new LocalDate(nextYear(date), JANUARY, REPORTING_MONTH_END_DAY);
            } else {
                return new LocalDate(date.getYear(), nextMonth(date), REPORTING_MONTH_END_DAY);
            }
        } else {
            return new LocalDate(date.getYear(), date.getMonthOfYear(), REPORTING_MONTH_END_DAY);
        }
    }

    public boolean isDateWithinCurrentReportMonth(LocalDate lastReportedDate) {
        return !(lastReportedDate.isBefore(startOfCurrentReportMonth(DateUtil.today())) ||
                lastReportedDate.isAfter(endOfCurrentReportMonth(DateUtil.today())));
    }

    private int previousMonth(LocalDate today) {
        return today.getMonthOfYear() - 1;
    }

    private int nextMonth(LocalDate today) {
        return today.getMonthOfYear() + 1;
    }

    private int previousYear(LocalDate today) {
        return today.getYear() - 1;
    }

    private int nextYear(LocalDate today) {
        return today.getYear() + 1;
    }
}

