package org.opensrp.register.service;

import org.opensrp.domain.MCTSServiceCode;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MCTSServiceCodeTest {
    @Test
    public void shouldCreateMCTSMessage() {
        String message = MCTSServiceCode.IFA.messageFor("THAYI1", DateUtil.newDate(2012, 3, 26));

        assertThat(message, is("ANMPW THAYI1 IFA 260312"));
    }
}
