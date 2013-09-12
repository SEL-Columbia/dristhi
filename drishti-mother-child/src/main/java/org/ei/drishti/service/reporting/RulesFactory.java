package org.ei.drishti.service.reporting;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.service.reporting.rules.AgeIsLessThanOneYearRule;
import org.ei.drishti.service.reporting.rules.EmptyRule;
import org.ei.drishti.service.reporting.rules.IRule;
import org.ei.drishti.service.reporting.rules.RelocationIsPermanentRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.ei.drishti.common.AllConstants.ReportingRuleNames.AGE_LESS_THAN_ONE_YEAR;
import static org.ei.drishti.common.AllConstants.ReportingRuleNames.RELOCATION_PERMANENT;

@Component
public class RulesFactory implements IRulesFactory{

    Map<String, IRule> rules = new HashMap<>();

    @Autowired
    public RulesFactory(AgeIsLessThanOneYearRule ageIsLessThanOneYearRule, RelocationIsPermanentRule relocationIsPermanentRule) {
        rules.put(AGE_LESS_THAN_ONE_YEAR, ageIsLessThanOneYearRule);
        rules.put(RELOCATION_PERMANENT, relocationIsPermanentRule);
    }

    public IRule ruleByName(String ruleName) throws Exception{
        if(rules.containsKey(ruleName)) {
            return rules.get(ruleName);
        }
        return new EmptyRule();
    }
}
