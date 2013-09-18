package org.ei.drishti.service.reporting;

import org.ei.drishti.service.reporting.rules.AgeIsLessThanOneYearRule;
import org.ei.drishti.service.reporting.rules.CurrentFPMethodIsCondomRule;
import org.ei.drishti.service.reporting.rules.IRule;
import org.ei.drishti.service.reporting.rules.RelocationIsPermanentRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.ei.drishti.common.AllConstants.ReportingRuleNames.*;

@Component
public class RulesFactory implements IRulesFactory {

    Map<String, IRule> rules = new HashMap<>();

    @Autowired
    public RulesFactory(AgeIsLessThanOneYearRule ageIsLessThanOneYearRule, RelocationIsPermanentRule relocationIsPermanentRule, CurrentFPMethodIsCondomRule currentFPMethodIsCondomRule) {
        rules.put(AGE_LESS_THAN_ONE_YEAR, ageIsLessThanOneYearRule);
        rules.put(RELOCATION_PERMANENT, relocationIsPermanentRule);
        rules.put(CURRENT_FP_METHOD_IS_CONDOM, currentFPMethodIsCondomRule);
    }

    public IRule ruleByName(String ruleName) throws RuleNotFoundException {
        if (rules.containsKey(ruleName)) {
            return rules.get(ruleName);
        }
        throw new RuleNotFoundException(ruleName);
    }
}
