package org.opensrp.common.audit;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.opensrp.common.audit.AuditMessageType.NORMAL;
import static org.opensrp.common.audit.AuditMessageType.SMS;

public class AuditMessageTypeTest {
    @Test
    public void shouldKnowWhichFieldsAreSupportedByAGivenTypeOfMessage() {
        assertThat(SMS.supports("message"), is(true));
        assertThat(SMS.supports("someRandomField"), is(false));

        assertThat(NORMAL.supports("data"), is(true));
        assertThat(NORMAL.supports("someOtherField"), is(false));
    }
}
