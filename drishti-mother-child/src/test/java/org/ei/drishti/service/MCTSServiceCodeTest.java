package org.ei.drishti.service;

import org.junit.Test;
import org.motechproject.util.DateUtil;

import static org.ei.drishti.service.MCTSSMSService.MCTSServiceCode;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MCTSServiceCodeTest {
    @Test
    public void shouldCreateMCTSMessage() {
        String message = MCTSServiceCode.IFA.messageFor("THAYI1", DateUtil.newDate(2012, 3, 26));

        assertThat(message, is("ANMPW THAYI1 IFA 260312"));
    }
}
