package org.ei.drishti.common.audit;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

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

    public AuditMessageBuilder audit(AuditMessageType type) {
        return new AuditMessageBuilder(this, type);
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

    private void createAuditMessage(AuditMessageType messageType, Map<String, String> data) {
        messages.add(new AuditMessage(DateTime.now(), messageIndex, messageType, data));
        messageIndex++;
        prune();
    }

    public class AuditMessageBuilder {
        private final Auditor auditor;
        private final AuditMessageType type;
        private Map<String, String> extraData;

        public AuditMessageBuilder(Auditor auditor, AuditMessageType type) {
            this.auditor = auditor;
            this.type = type;
            this.extraData = new HashMap<String, String>();
        }

        public AuditMessageBuilder with(String key, String value) {
            if (!type.supports(key)) {
                throw new ForbiddenFieldInAuditMessage(type, key, value);
            }
            extraData.put(key, value);
            return this;
        }

        public void done() {
            auditor.createAuditMessage(type, extraData);
        }
    }
}
