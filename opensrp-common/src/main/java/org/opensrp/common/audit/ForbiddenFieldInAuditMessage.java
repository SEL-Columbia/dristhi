package org.opensrp.common.audit;

import static java.text.MessageFormat.format;

public class ForbiddenFieldInAuditMessage extends RuntimeException {
    public ForbiddenFieldInAuditMessage(AuditMessageType type, String key, String value) {
        super(format("Not allowed to add field: {0} => {1} for audit message of type: {2}", key, value, type));
    }
}
