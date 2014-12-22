package org.ei.drishti.common.audit;

import org.junit.Test;
import sun.text.resources.sr.FormatData_sr_BA;

import static org.ei.drishti.common.audit.AuditMessageType.FORM_SUBMISSION;
import static org.ei.drishti.common.audit.AuditMessageType.NORMAL;
import static org.ei.drishti.common.audit.AuditMessageType.SMS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AuditMessageTypeTest {
    @Test
    public void shouldKnowWhichFieldsAreSupportedByNornalTypeOfMessage() {
        assertThat(NORMAL.supports("data"), is(true));
        assertThat(NORMAL.supports("someOtherField"), is(false));
    }

    @Test
    public void shouldKnowWhichFieldsAreSupportedByFormSubmissionTypeOfMessage() {
        assertThat(FORM_SUBMISSION.supports("formId"), is(true));
        assertThat(FORM_SUBMISSION.supports("formType"), is(true));
        assertThat(FORM_SUBMISSION.supports("formData"), is(true));
        assertThat(FORM_SUBMISSION.supports("someRandomField"), is(false));
    }

    @Test
    public void shouldKnowWhichFieldsAreSupportedBySmsTypeOfMessage() {
        assertThat(SMS.supports("recipient"), is(true));
        assertThat(SMS.supports("message"), is(true));
        assertThat(SMS.supports("smsIsSent"), is(true));
        assertThat(SMS.supports("someRandomField"), is(false));
    }
}
