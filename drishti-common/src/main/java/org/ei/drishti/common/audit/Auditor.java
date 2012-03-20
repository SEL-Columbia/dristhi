package org.ei.drishti.common.audit;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.binarySearch;
import static org.ei.drishti.common.audit.AuditMessageType.NORMAL;

@Scope("singleton")
@Component
public class Auditor {
    private List<AuditMessage> messages;
    private final int numberOfAuditMessagesToHoldOnTo;
    private long messageIndex;

    @Autowired
    public Auditor(@Value("#{drishti['number.of.audit.messages']}") int numberOfAuditMessagesToHoldOnTo) {
        this.messages = new ArrayList<AuditMessage>();
        this.numberOfAuditMessagesToHoldOnTo = numberOfAuditMessagesToHoldOnTo;
        this.messageIndex = 0;
    }

    public void audit(AuditMessageType type, String message, String... extraData) {
        messages.add(new AuditMessage(DateTime.now(), messageIndex, type, message, extraData));
        messageIndex++;
        prune();
    }

    public List<AuditMessage> messagesSince(long messageIndex) {
        if (messageIndex <= 0) {
            return messages;
        }

        int position = Math.abs(binarySearch(messages, new AuditMessage(DateTime.now(), messageIndex, NORMAL, null)));

        if (position >= messages.size()) {
            return Collections.emptyList();
        }

        return messages.subList(position + 1, messages.size());
    }

    private void prune() {
        if (messages.size() > numberOfAuditMessagesToHoldOnTo) {
            messages.remove(0);
        }
    }

}
