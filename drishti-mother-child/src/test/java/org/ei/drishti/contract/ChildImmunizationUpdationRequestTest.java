package org.ei.drishti.contract;

import org.joda.time.LocalDate;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ChildImmunizationUpdationRequestTest {
    @Test
    public void shouldParseLocalDateFromISOFormattedDate() throws Exception {
        ChildImmunizationUpdationRequest careInformation = new ChildImmunizationUpdationRequest("CASE X", "ANM 1", "OPV", "2012-09-04T08:42:17Z");

        assertEquals(LocalDate.parse("2012-09-04"), careInformation.visitDate());
    }
}
