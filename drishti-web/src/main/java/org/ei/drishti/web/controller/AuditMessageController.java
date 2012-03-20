package org.ei.drishti.web.controller;

import ch.lambdaj.function.convert.Converter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ei.drishti.common.audit.AuditMessage;
import org.ei.drishti.common.audit.AuditMessageType;
import org.ei.drishti.common.audit.Auditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

@Controller
public class AuditMessageController {
    private final Auditor auditor;

    @Autowired
    public AuditMessageController(Auditor auditor) {
        this.auditor = auditor;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/audit/messages")
    @ResponseBody
    public List<AuditMessageItem> getAuditMessages(@RequestParam("previousAuditMessageIndex") long previousIndex) throws IOException {
        List<AuditMessage> messages = auditor.messagesSince(previousIndex);

        return with(messages).convert(new Converter<AuditMessage, AuditMessageItem>() {
            @Override
            public AuditMessageItem convert(AuditMessage auditMessage) {
                return AuditMessageItem.from(auditMessage);
            }
        });
    }

    protected static class AuditMessageItem {
        @JsonProperty
        private final long index;
        @JsonProperty
        private final AuditMessageType type;
        @JsonProperty
        private final String message;
        @JsonProperty
        private final List<String> data;

        public AuditMessageItem(long index, AuditMessageType type, String message, String... data) {
            this.index = index;
            this.type = type;
            this.message = message;
            this.data = Arrays.asList(data);
        }

        public static AuditMessageItem from(AuditMessage auditMessage) {
            return new AuditMessageItem(auditMessage.index(), auditMessage.type(), auditMessage.message(), auditMessage.data());
        }
    }
}
