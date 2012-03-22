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

    @Autowired
    public Auditor(@Value("#{drishti['number.of.audit.messages']}") int numberOfAuditMessagesToHoldOnTo) {
        this.messages = new ArrayList<AuditMessage>();
        this.numberOfAuditMessagesToHoldOnTo = numberOfAuditMessagesToHoldOnTo;
    }

    public AuditMessageBuilder audit(AuditMessageType type) {
        return new AuditMessageBuilder(this, type);
    }

    public List<AuditMessage> messagesSince(long messageIndex) {
        if (messageIndex <= 0) {
            return messages;
        }

        int index = binarySearch(messages, new AuditMessage(DateTime.now(), messageIndex, NORMAL, null));
        int position = Math.abs(index + 1);

        if (position >= messages.size()) {
            return Collections.emptyList();
        }

        return messages.subList(position, messages.size());
    }

    private void prune() {
        if (messages.size() > numberOfAuditMessagesToHoldOnTo) {
            messages.remove(0);
        }
    }

    private void createAuditMessage(AuditMessageType messageType, Map<String, String> data) {
        messages.add(new AuditMessage(DateTime.now(), DateTime.now().getMillis(), messageType, data));
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
