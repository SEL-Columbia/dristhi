package org.opensrp.common.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by real on 11/07/17.
 */
public class ReportDataUpdateRequestTest {
    @Test
    public void testConstructorNGettersOfReportDataUpdateRequest() {
        ReportDataUpdateRequest reportDataUpdateRequest, reportDataUpdateRequest2, reportDataUpdateRequest3;
        reportDataUpdateRequest = new ReportDataUpdateRequest();

        reportDataUpdateRequest.withIndicator("gogo");
        assertEquals("gogo", reportDataUpdateRequest.indicator());
        assertNotSame("nono", reportDataUpdateRequest.indicator());

        reportDataUpdateRequest.withStartDate("2017-07-11");
        assertEquals("2017-07-11", reportDataUpdateRequest.startDate());
        assertNotSame("nono", reportDataUpdateRequest.startDate());

        reportDataUpdateRequest.withEndDate("2017-12-11");
        assertEquals("2017-12-11", reportDataUpdateRequest.endDate());
        assertNotSame("nono", reportDataUpdateRequest.endDate());

        reportDataUpdateRequest.withType("easy");
        assertEquals("easy", reportDataUpdateRequest.type());
        assertNotSame("nono", reportDataUpdateRequest.type());

        List<ReportingData> reportingDataList = new ArrayList<>();
        ReportingData reportingData1 = new ReportingData("report");
        reportingDataList.add(reportingData1);
        reportDataUpdateRequest.withReportingData(reportingDataList);
        assertTrue(reportDataUpdateRequest.reportingData().get(0).toString().contains("type=report"));
        assertFalse(reportDataUpdateRequest.reportingData().get(0).toString().contains("no report"));

        reportDataUpdateRequest2 = new ReportDataUpdateRequest("new report");
        assertEquals("new report", reportDataUpdateRequest2.type());
        assertNotSame("no report found", reportDataUpdateRequest2.type());
        System.out.println(reportDataUpdateRequest2.toString());
        assertTrue(reportDataUpdateRequest2.toString().contains("type=new report"));
        assertFalse(reportDataUpdateRequest2.toString().contains("type=null"));

        reportDataUpdateRequest3 = ReportDataUpdateRequest.buildReportDataRequest("file", Indicator.ANC,
                "2017-07-11", "2017-12-11", reportingDataList);
        assertEquals("ANC", reportDataUpdateRequest3.indicator());
        assertNotSame("PNC", reportDataUpdateRequest3.indicator());
    }


    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ReportDataUpdateRequest.class)
                .verify();
    }
}
