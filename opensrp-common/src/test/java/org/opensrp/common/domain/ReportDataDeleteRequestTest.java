package org.opensrp.common.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by real on 11/07/17.
 */
public class ReportDataDeleteRequestTest {

    @Test
    public void testConstructorNGetters() {
        ReportDataDeleteRequest reportDataDeleteRequest1, reportDataDeleteRequest2, reportDataDeleteRequest3;
        reportDataDeleteRequest1 = new ReportDataDeleteRequest();
        reportDataDeleteRequest2 = new ReportDataDeleteRequest();

        reportDataDeleteRequest1.withType("java");
        assertEquals("java", reportDataDeleteRequest1.type());
        assertNotSame("c++", reportDataDeleteRequest1.type());

        reportDataDeleteRequest2.withDristhiEntityId("1234");
        assertEquals("1234", reportDataDeleteRequest2.dristhiEntityId());
        assertNotSame("4321", reportDataDeleteRequest2.dristhiEntityId());

        reportDataDeleteRequest3 = new ReportDataDeleteRequest("python");
        assertEquals("python", reportDataDeleteRequest3.type());
        assertNotSame("c++", reportDataDeleteRequest3.type());
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ReportDataDeleteRequest.class)
                .verify();
    }

    @Test
    public void testToStringOfReportDataDeleteRequest() {
        ReportDataDeleteRequest reportDataDeleteRequest = new ReportDataDeleteRequest("java");
        assertTrue(reportDataDeleteRequest.toString().contains("java"));
        assertFalse(reportDataDeleteRequest.toString().contains("c++"));
    }

    @Test
    public void testServiceProvidedDataDeleteRequest() {
        ReportDataDeleteRequest reportDataDeleteRequest = ReportDataDeleteRequest.serviceProvidedDataDeleteRequest("abc");
        assertEquals("serviceProvided", reportDataDeleteRequest.type());
        assertNotSame("provider", reportDataDeleteRequest.type());
        assertEquals("abc", reportDataDeleteRequest.dristhiEntityId());
        assertNotSame("cba", reportDataDeleteRequest.dristhiEntityId());
    }

    @Test
    public void testAnmReportDataDeleteRequest() {
        ReportDataDeleteRequest reportDataDeleteRequest = ReportDataDeleteRequest.anmReportDataDeleteRequest("abc");
        assertEquals("anmReportData", reportDataDeleteRequest.type());
        assertNotSame("provider", reportDataDeleteRequest.type());
        assertEquals("abc", reportDataDeleteRequest.dristhiEntityId());
        assertNotSame("cba", reportDataDeleteRequest.dristhiEntityId());
    }


}
