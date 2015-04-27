package org.opensrp.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.motechproject.util.DateUtil;
import org.opensrp.domain.MCTSServiceCode;

public class MCTSServiceCodeTest {
    @Test
    public void shouldCreateMCTSMessage() {
        String message = MCTSServiceCode.IFA.messageFor("THAYI1", DateUtil.newDate(2012, 3, 26));

        assertThat(message, is("ANMPW THAYI1 IFA 260312"));
    }
}
