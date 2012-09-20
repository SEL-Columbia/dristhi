package org.ei.drishti.contract;

import org.joda.time.LocalDate;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class AnteNatalCareInformationTest {
    @Test
    public void shouldParseLocalDateFromSubmissionDate() throws Exception {
        AnteNatalCareInformation careInformation = new AnteNatalCareInformation("CASE X", "ANM 1", 1, "2012-09-04");

        assertEquals(LocalDate.parse("2012-09-04"), careInformation.visitDate());
    }
}
