package org.opensrp.domain;

import org.opensrp.common.util.DateUtil;
import org.opensrp.common.util.EasyMap;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import org.opensrp.domain.EligibleCouple;

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
