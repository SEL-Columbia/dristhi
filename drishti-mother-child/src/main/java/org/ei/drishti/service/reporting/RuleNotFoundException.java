package org.ei.drishti.service.reporting;

import java.text.MessageFormat;

public class RuleNotFoundException extends RuntimeException {
    public RuleNotFoundException(String ruleName) {
        super(MessageFormat.format("Could not find rule by name: {0}. " +
                "Are you sure there is a class by name: {0} and there is a mapping for this class in RulesFactory?", ruleName));
    }
}
