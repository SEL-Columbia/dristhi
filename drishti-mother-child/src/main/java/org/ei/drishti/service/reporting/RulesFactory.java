package org.ei.drishti.service.reporting;

import org.ei.drishti.service.reporting.rules.EmptyRule;
import org.ei.drishti.service.reporting.rules.IRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

@Component
public class RulesFactory implements IRulesFactory{

    private static Logger logger = LoggerFactory.getLogger(RulesFactory.class);

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
            logger.error(String.format("Class for {0} mentioned is not found. Message: {1}", ruleName,e.getMessage()));
            logger.error(getFullStackTrace(e));
            throw e;
        } catch (InstantiationException e) {
            logger.error(String.format("Class for {0} cannot be instantiated. Message: {1}", ruleName, e.getMessage()));
            logger.error(getFullStackTrace(e));
            throw e;
        } catch (IllegalAccessException e) {
            logger.error(String.format("Class for {0} cannot be accessed. Message: {1}", ruleName, e.getMessage()));
            logger.error(getFullStackTrace(e));
            throw e;
        }
        return rule;
    }
}
