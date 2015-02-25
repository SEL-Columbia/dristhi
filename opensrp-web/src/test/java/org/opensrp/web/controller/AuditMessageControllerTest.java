package org.opensrp.web.controller;

import org.opensrp.common.audit.AuditMessage;
import org.opensrp.common.audit.Auditor;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.util.DateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opensrp.common.audit.AuditMessageType.FORM_SUBMISSION;
import static org.opensrp.common.audit.AuditMessageType.SMS;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.delivery.schedule.util.SameItems.hasSameItemsAs;
import org.opensrp.web.controller.AuditMessageController;
import org.opensrp.web.controller.AuditMessageController.AuditMessageItem;

public class AuditMessageControllerTest {
    @Mock
    Auditor auditor;
    private DateTime now;
    private DateTime yesterday;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        now = DateUtil.now();
        yesterday = DateUtil.now().minusDays(1);
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

        expectedItems.add(new AuditMessageItem(now, 11, SMS, smsData()));
        expectedItems.add(new AuditMessageItem(yesterday, 12, FORM_SUBMISSION, formData()));

        return expectedItems;
    }

    private List<AuditMessage> messages() {
        ArrayList<AuditMessage> messages = new ArrayList<AuditMessage>();

        messages.add(new AuditMessage(now, 11, SMS, smsData()));
        messages.add(new AuditMessage(yesterday, 12, FORM_SUBMISSION, formData()));

        return messages;
    }

    private HashMap<String, String> formData() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("data", "Form 1");
        map.put("someOtherData", "Def");
        return map;
    }

    private Map<String, String> smsData() {
        Map<String,String> map = new HashMap<String, String>();
        map.put("recipient", "Abc");
        map.put("message", "SMS 1");
        return map;
    }

}
