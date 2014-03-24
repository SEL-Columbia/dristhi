package org.ei.drishti.domain;

import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.common.util.EasyMap;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EligibleCoupleTest {

    private EligibleCouple ec;

    @Test
    public void shouldReturnWifeDOBWhenWifeDOBExists() {
        DateUtil.fakeIt(LocalDate.parse("2014-01-01"));

        ec = new EligibleCouple("CASE X", "EC Number 1")
                .withDetails(EasyMap.mapOf("womanDOB", "1988-01-01"));

        String dob = ec.wifeDOB();

        assertEquals("1988-01-01", dob);
    }

    @Test
    public void shouldCalculateDOBForGivenAge() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2014-01-01"));

        ec = new EligibleCouple("CASE X", "EC Number 1")
                .withDetails(EasyMap.mapOf("wifeAge", "26"));

        String dob = ec.wifeDOB();

        assertEquals("1988-01-01", dob);
    }
}
