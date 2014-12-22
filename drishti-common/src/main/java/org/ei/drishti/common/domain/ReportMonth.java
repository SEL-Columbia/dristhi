package org.ei.drishti.common.domain;

import org.ei.drishti.common.util.DateUtil;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.ei.drishti.common.AllConstants.Report.*;

@Component
public class ReportMonth {
    private static final int JANUARY = 1;
    private static final int DECEMBER = 12;

    public Date startDateOfReportingYear() {
        int reportingYear = reportingYear();
        return new LocalDate().withDayOfMonth(REPORTING_MONTH_START_DAY).withMonthOfYear(FIRST_REPORT_MONTH_OF_YEAR).withYear(reportingYear).toDate();
    }

    public int reportingYear() {
        LocalDate now = DateUtil.today();
        LocalDate beginningOfReportingYear = DateUtil.today().withMonthOfYear(FIRST_REPORT_MONTH_OF_YEAR).withDayOfMonth(REPORTING_MONTH_START_DAY);
        return now.isBefore(beginningOfReportingYear) ? previousYear(now) : now.getYear();
    }

    public LocalDate startDateOfNextReportingMonth(LocalDate date) {
        if (date.getDayOfMonth() < REPORTING_MONTH_START_DAY) {
            return new LocalDate(date.getYear(), date.getMonthOfYear(), REPORTING_MONTH_START_DAY);
        }
        if (date.getMonthOfYear() == DECEMBER) {
            return new LocalDate(date.plusYears(1).getYear(), JANUARY, REPORTING_MONTH_START_DAY);
        }
        return new LocalDate(date.getYear(), date.plusMonths(1).getMonthOfYear(), REPORTING_MONTH_START_DAY);
    }

    public LocalDate endDateOfReportingMonthGivenStartDate(LocalDate startDate) {
        return startDate.plusMonths(1).minusDays(1);
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
        return areDatesBelongToSameReportingMonth(lastReportedDate, DateUtil.today());
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

    public int reportingMonth(LocalDate date) {
        return endOfCurrentReportMonth(date).getMonthOfYear();
    }

    public int reportingYear(LocalDate date) {
        return endOfCurrentReportMonth(date).getYear();
    }

    public boolean areDatesBelongToSameReportingMonth(LocalDate date, LocalDate anotherDate) {
        return !(date.isBefore(startOfCurrentReportMonth(anotherDate)) ||
                date.isAfter(endOfCurrentReportMonth(anotherDate)));
    }
}

