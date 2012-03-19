package org.ei.drishti.common.audit;

public class AuditMessage implements Comparable<AuditMessage> {
    private final long messageIndex;
    private final AuditMessageType type;
    private final String message;

    public AuditMessage(long messageIndex, AuditMessageType type, String message) {
        this.messageIndex = messageIndex;
        this.type = type;
        this.message = message;
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
