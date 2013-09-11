package org.ei.drishti.service.reporting;

import org.ei.drishti.service.reporting.rules.EmptyRule;
import org.ei.drishti.service.reporting.rules.IRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RulesFactory implements IRulesFactory{

    Map<String, IRule> rules = new HashMap<>();

    public IRule ruleByName(String ruleName) throws Exception{
        if(rules.containsKey(ruleName.toLowerCase())) {
            return rules.get(ruleName);
        }
        IRule rule = new EmptyRule();
        try {
            String packageName = String.valueOf(IRule.class.getPackage().getName());
            Class ruleClass = Class.forName(packageName + "." + ruleName);
            rule = (IRule) ruleClass.newInstance();
            rules.put(ruleName.toLowerCase(), rule);
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (InstantiationException e) {
            throw e;
        } catch (IllegalAccessException e) {
            throw e;
        }
        return rule;
    }
}
