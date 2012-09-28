package org.ei.drishti.contract;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class AnteNatalCareInformationTest {
    @Test
    public void shouldParseLocalDateFromSubmissionDate() throws Exception {
        AnteNatalCareInformation careInformation = new AnteNatalCareInformation("CASE X", "ANM 1", 1, "2012-09-04");

        assertEquals(LocalDate.parse("2012-09-04"), careInformation.visitDate());
    }

    @Test
    public void shouldParseWasTTShotGiven() throws Exception {
        AnteNatalCareInformation careInformation = new AnteNatalCareInformation("CASE X", "ANM 1", 1, "2012-09-04").withTTDose("TT2");
        assertTrue(careInformation.wasTTShotProvided());
        careInformation = new AnteNatalCareInformation("CASE X", "ANM 1", 1, "2012-09-04").withTTDose("");
        assertFalse(careInformation.wasTTShotProvided());
        careInformation = new AnteNatalCareInformation("CASE X", "ANM 1", 1, "2012-09-04").withTTDose(null);
        assertFalse(careInformation.wasTTShotProvided());
    }
}
