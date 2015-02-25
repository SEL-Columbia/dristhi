package org.opensrp.service.reporting;

import org.opensrp.service.reporting.rules.IRule;

public interface IRulesFactory {
    public IRule ruleByName(String ruleName) throws Exception;
}
