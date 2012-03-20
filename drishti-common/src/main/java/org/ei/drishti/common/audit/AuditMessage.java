package org.ei.drishti.common.audit;

import org.joda.time.DateTime;

public class AuditMessage implements Comparable<AuditMessage> {
    private final DateTime time;
    private final long messageIndex;
    private final AuditMessageType type;
    private final String message;
    private final String[] extraData;

    public AuditMessage(DateTime time, long messageIndex, AuditMessageType type, String message, String... extraData) {
        this.time = time;
        this.messageIndex = messageIndex;
        this.type = type;
        this.message = message;
        this.extraData = extraData;
    }

    public String message() {
        return message;
    }

    public AuditMessageType type() {
        return type;
    }

    public long index() {
        return messageIndex;
    }

    public String[] data() {
        return extraData;
    }

    public DateTime time() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditMessage that = (AuditMessage) o;

        return messageIndex == that.messageIndex;
    }

    @Override
    public int hashCode() {
        return (int) (messageIndex ^ (messageIndex >>> 32));
    }

    @Override
    public int compareTo(AuditMessage other) {
        return Long.valueOf(messageIndex - other.messageIndex).intValue();
    }
}
