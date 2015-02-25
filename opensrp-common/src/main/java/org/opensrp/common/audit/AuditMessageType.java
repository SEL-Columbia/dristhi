package org.opensrp.common.audit;

import java.util.Arrays;
import java.util.List;

public enum AuditMessageType {
    NORMAL("data"),
    FORM_SUBMISSION("formId", "formType", "formData"),
    SMS("recipient", "message", "smsIsSent"),
    ;

    private final List<String> supportedFields;

    AuditMessageType(String... supportedFields) {
        this.supportedFields = Arrays.asList(supportedFields);
    }

    public boolean supports(String key) {
        return supportedFields.contains(key);
    }
}
