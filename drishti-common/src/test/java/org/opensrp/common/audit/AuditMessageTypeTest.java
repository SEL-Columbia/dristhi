package org.ei.drishti.common.audit;

import org.junit.Test;

import static org.ei.drishti.common.audit.AuditMessageType.NORMAL;
import static org.ei.drishti.common.audit.AuditMessageType.SMS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AuditMessageTypeTest {
    @Test
    public void shouldKnowWhichFieldsAreSupportedByAGivenTypeOfMessage() {
        assertThat(SMS.supports("message"), is(true));
        assertThat(SMS.supports("someRandomField"), is(false));

        assertThat(NORMAL.supports("data"), is(true));
        assertThat(NORMAL.supports("someOtherField"), is(false));
    }
}
