package org.ei.drishti.web.controller;

import org.ei.drishti.common.audit.AuditMessage;
import org.ei.drishti.common.audit.Auditor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ei.drishti.common.audit.AuditMessageType.FORM_SUBMISSION;
import static org.ei.drishti.common.audit.AuditMessageType.SMS;
import static org.ei.drishti.integration.util.SameItems.hasSameItemsAs;
import static org.ei.drishti.web.controller.AuditMessageController.AuditMessageItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuditMessageControllerTest {
    @Mock
    Auditor auditor;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetAuditMessagesSinceGivenTime() throws IOException {
        AuditMessageController controller = new AuditMessageController(auditor);
        when(auditor.messagesSince(10)).thenReturn(messages());

        List<AuditMessageItem> messageItems = controller.getAuditMessages(10);

        assertThat(messageItems, hasSameItemsAs(expectedMessageItems()));
    }

    private List<AuditMessageItem> expectedMessageItems() {
        final List<AuditMessageItem> expectedItems = new ArrayList<AuditMessageItem>();
        expectedItems.add(new AuditMessageItem(11, SMS, "SMS 1", "Abc"));
        expectedItems.add(new AuditMessageItem(12, FORM_SUBMISSION, "Form 1", "Def"));
        return expectedItems;
    }

    private List<AuditMessage> messages() {
        ArrayList<AuditMessage> messages = new ArrayList<AuditMessage>();
        messages.add(new AuditMessage(11, SMS, "SMS 1", "Abc"));
        messages.add(new AuditMessage(12, FORM_SUBMISSION, "Form 1", "Def"));
        return messages;
    }

}
