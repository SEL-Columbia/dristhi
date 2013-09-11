package org.ei.drishti.service.reporting;

import org.ei.drishti.service.reporting.rules.IRule;

public interface IRulesFactory {
    public IRule ruleByName(String ruleName) throws Exception;
}
